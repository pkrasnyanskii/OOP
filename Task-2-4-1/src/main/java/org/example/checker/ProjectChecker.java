package org.example.checker;

import org.example.model.*;
import org.example.model.results.BuildStatus;
import org.example.model.results.StudentCheckResult;
import org.example.model.results.TaskCheckResult;
import org.example.model.results.TestCounts;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class ProjectChecker {
    private static final Logger log = Logger.getLogger(ProjectChecker.class.getName());

    private final OopCheckerConfig config;
    private final GitManager gitManager;
    private final ScoreCalculator scoreCalc;
    private final ActivityTracker activityTracker;
    private final ProcessRunner processRunner;
    private final Path reposDir;

    public ProjectChecker(OopCheckerConfig config, Path reposDir) {
        this.config = config;
        this.reposDir = reposDir;
        int timeout = config.getScoringConfig().getTestTimeoutSeconds();
        this.gitManager = new GitManager(reposDir, timeout);
        this.scoreCalc = new ScoreCalculator(config);
        this.activityTracker = new ActivityTracker(gitManager);
        this.processRunner = new ProcessRunner(timeout);
    }

    public List<StudentCheckResult> runChecks() {
        List<StudentCheckResult> results = new ArrayList<>();
        CheckInstruction instruction = config.getCheckInstruction();

        for (String github : instruction.getStudentGithubs()) {
            Optional<Student> studentOpt = config.findStudentByGithub(github);
            if (studentOpt.isEmpty()) {
                log.warning("Student not found in config: " + github);
                continue;
            }
            Student student = studentOpt.get();
            String groupName = config.findGroupByStudentGithub(github)
                    .map(Group::getName).orElse("Unknown");

            StudentCheckResult studentResult = new StudentCheckResult(github, student.getFullName(), groupName);

            Path repoPath;
            try {
                repoPath = gitManager.cloneOrUpdate(github, student.getRepoUrl());
            } catch (Exception e) {
                log.severe("Failed to clone repo for " + github + ": " + e.getMessage());
                results.add(studentResult);
                continue;
            }

            for (String taskId : instruction.getTaskIds()) {
                TaskCheckResult taskResult = checkTask(repoPath, github, taskId);
                taskResult.setScore(scoreCalc.calculate(github, taskResult));
                studentResult.addTaskResult(taskResult);
            }

            // Activity tracking (bonus task)
            ActivityConfig activityConfig = config.getActivityConfig();
            if (activityConfig != null) {
                int activeWeeks = activityTracker.countActiveWeeks(repoPath, activityConfig);
                double activityBonus = activityTracker.calculateActivityBonus(activeWeeks, activityConfig);
                studentResult.setActiveWeeks(activeWeeks);
                studentResult.setActivityBonus(activityBonus);
            }

            results.add(studentResult);
        }

        return results;
    }

    private TaskCheckResult checkTask(Path repoPath, String github, String taskId) {
        TaskCheckResult result = new TaskCheckResult(taskId);

        // Last commit date for this task directory
        LocalDate lastCommit = gitManager.getLastCommitDate(repoPath, taskId);
        result.setLastCommitDate(lastCommit);

        // Step 1: Compile
        log.info("[" + github + "/" + taskId + "] Compiling...");
        ProcessResult compile = runGradle(repoPath, ":" + taskId + ":compileJava");
        result.setCompileOutput(compile.getOutput());
        if (compile.isTimedOut()) {
            result.setCompileStatus(BuildStatus.TIMEOUT);
            return result;
        }
        if (!compile.isSuccess()) {
            result.setCompileStatus(BuildStatus.FAILED);
            return result;
        }
        result.setCompileStatus(BuildStatus.SUCCESS);

        // Step 2: Javadoc
        log.info("[" + github + "/" + taskId + "] Generating javadoc...");
        ProcessResult docs = runGradle(repoPath, ":" + taskId + ":javadoc");
        result.setDocsOutput(docs.getOutput());
        if (docs.isTimedOut()) {
            result.setDocsStatus(BuildStatus.TIMEOUT);
        } else if (isTaskNotFound(docs.getOutput())) {
            result.setDocsStatus(BuildStatus.NOT_AVAILABLE);
        } else {
            result.setDocsStatus(docs.isSuccess() ? BuildStatus.SUCCESS : BuildStatus.FAILED);
        }

        // Step 2: Checkstyle (Google Java Style)
        log.info("[" + github + "/" + taskId + "] Checking style...");
        ProcessResult style = runGradle(repoPath, ":" + taskId + ":checkstyleMain");
        result.setStyleOutput(style.getOutput());
        if (style.isTimedOut()) {
            result.setStyleStatus(BuildStatus.TIMEOUT);
        } else if (isTaskNotFound(style.getOutput())) {
            result.setStyleStatus(BuildStatus.NOT_AVAILABLE);
        } else {
            result.setStyleStatus(style.isSuccess() ? BuildStatus.SUCCESS : BuildStatus.FAILED);
        }

        // Step 3: Tests
        log.info("[" + github + "/" + taskId + "] Running tests...");
        ProcessResult tests = runGradle(repoPath, ":" + taskId + ":test", "--continue");
        result.setTestOutput(tests.getOutput());
        if (tests.isTimedOut()) {
            result.setTestStatus(BuildStatus.TIMEOUT);
        } else {
            result.setTestStatus(BuildStatus.SUCCESS); // parse XML for actual counts
            TestCounts counts = parseTestResults(repoPath, taskId);
            result.setTestCounts(counts);
            if (counts.getFailed() > 0 || counts.getSkipped() > 0) {
                result.setTestStatus(BuildStatus.FAILED);
            }
        }

        return result;
    }

    private ProcessResult runGradle(Path repoPath, String... tasks) {
        List<String> cmd = new ArrayList<>();
        cmd.add(ProcessRunner.gradlewCommand());
        cmd.addAll(Arrays.asList(tasks));
        try {
            return processRunner.run(repoPath, cmd);
        } catch (IOException | InterruptedException e) {
            return new ProcessResult(-1, e.getMessage(), false);
        }
    }

    private boolean isTaskNotFound(String output) {
        return output.contains("Task '" ) && output.contains("' not found")
                || output.contains("Could not find") && output.contains("task");
    }

    /**
     * Parse JUnit XML test result files from build/test-results/.
     */
    private TestCounts parseTestResults(Path repoPath, String taskId) {
        TestCounts total = new TestCounts();
        Path testResultsDir = repoPath.resolve(taskId).resolve("build").resolve("test-results");

        if (!Files.exists(testResultsDir)) {
            return total;
        }

        try (Stream<Path> stream = Files.walk(testResultsDir)) {
            stream.filter(p -> p.toString().endsWith(".xml"))
                  .forEach(xml -> {
                      try {
                          TestCounts counts = parseXmlResults(xml.toFile());
                          total.add(counts);
                      } catch (Exception e) {
                          log.warning("Failed to parse test XML " + xml + ": " + e.getMessage());
                      }
                  });
        } catch (IOException e) {
            log.warning("Failed to walk test results dir: " + e.getMessage());
        }

        return total;
    }

    private TestCounts parseXmlResults(File xmlFile) throws Exception {
        Document doc = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(xmlFile);

        NodeList suites = doc.getElementsByTagName("testsuite");
        int passed = 0, failed = 0, skipped = 0;

        for (int i = 0; i < suites.getLength(); i++) {
            Element suite = (Element) suites.item(i);
            int tests = intAttr(suite, "tests");
            int failures = intAttr(suite, "failures");
            int errors = intAttr(suite, "errors");
            int skip = intAttr(suite, "skipped");
            failed += failures + errors;
            skipped += skip;
            passed += tests - failures - errors - skip;
        }

        return new TestCounts(Math.max(0, passed), failed, skipped);
    }

    private int intAttr(Element el, String attr) {
        String val = el.getAttribute(attr);
        if (val == null || val.isEmpty()) return 0;
        try { return Integer.parseInt(val); } catch (NumberFormatException e) { return 0; }
    }

    // Expose for use in Main
    public Path getReposDir() { return reposDir; }
}

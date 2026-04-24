package ru.nsu.krasnyanskii.checker;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import ru.nsu.krasnyanskii.model.ActivityConfig;
import ru.nsu.krasnyanskii.model.CheckInstruction;
import ru.nsu.krasnyanskii.model.Group;
import ru.nsu.krasnyanskii.model.OopCheckerConfig;
import ru.nsu.krasnyanskii.model.Student;
import ru.nsu.krasnyanskii.model.results.BuildStatus;
import ru.nsu.krasnyanskii.model.results.StudentCheckResult;
import ru.nsu.krasnyanskii.model.results.TaskCheckResult;
import ru.nsu.krasnyanskii.model.results.TestCounts;

/**
 * Orchestrates the check pipeline for all students and tasks.
 * Pipeline per task: (1) compile, (2) javadoc + checkstyle, (3) tests.
 * Each step is skipped if the previous step failed.
 */
public class ProjectChecker {
    private static final Logger log = Logger.getLogger(ProjectChecker.class.getName());

    private final OopCheckerConfig config;
    private final GitManager       gitManager;
    private final ScoreCalculator  scoreCalc;
    private final ActivityTracker  activityTracker;
    private final ProcessRunner    processRunner;

    /**
     * Creates a ProjectChecker with the given config and local repos directory.
     *
     * @param config   parsed OOP checker configuration
     * @param reposDir directory where student repos will be cloned
     */
    public ProjectChecker(OopCheckerConfig config, Path reposDir) {
        this.config    = config;
        int timeout    = config.getScoringConfig().getTestTimeoutSeconds();
        this.gitManager      = new GitManager(reposDir, timeout);
        this.scoreCalc       = new ScoreCalculator(config);
        this.activityTracker = new ActivityTracker(gitManager);
        this.processRunner   = new ProcessRunner(timeout);
    }

    /**
     * Runs checks for all students listed in the config's check instruction.
     *
     * @return list of per-student check results
     */
    public List<StudentCheckResult> runChecks() {
        List<StudentCheckResult> results = new ArrayList<>();
        CheckInstruction instruction = config.getCheckInstruction();

        for (String github : instruction.getStudentGithubs()) {
            Optional<Student> studentOpt = config.findStudentByGithub(github);
            if (studentOpt.isEmpty()) {
                log.warning("Student not found in config: " + github);
                continue;
            }
            Student student  = studentOpt.get();
            String groupName = config.findGroupByStudentGithub(github)
                    .map(Group::getName).orElse("Unknown");

            StudentCheckResult studentResult =
                    new StudentCheckResult(github, student.getFullName(), groupName);

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

            // Bonus task: weekly activity
            ActivityConfig activityConfig = config.getActivityConfig();
            if (activityConfig != null) {
                int    activeWeeks   = activityTracker.countActiveWeeks(repoPath, activityConfig);
                double activityBonus = activityTracker.calculateActivityBonus(
                        activeWeeks, activityConfig);
                studentResult.setActiveWeeks(activeWeeks);
                studentResult.setActivityBonus(activityBonus);
            }

            results.add(studentResult);
        }

        return results;
    }

    // ── Pipeline steps ────────────────────────────────────────────────────────

    private TaskCheckResult checkTask(Path repoPath, String github, String taskId) {
        TaskCheckResult result = new TaskCheckResult(taskId);
        result.setLastCommitDate(gitManager.getLastCommitDate(repoPath, taskId));

        // Step 1: compile
        log.info("[" + github + "/" + taskId + "] Step 1: compile");
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

        // Step 2: javadoc + checkstyle (only if compile passed)
        log.info("[" + github + "/" + taskId + "] Step 2: javadoc");
        ProcessResult docs = runGradle(repoPath, ":" + taskId + ":javadoc");
        result.setDocsOutput(docs.getOutput());
        result.setDocsStatus(resolveStatus(docs));

        log.info("[" + github + "/" + taskId + "] Step 2: checkstyle");
        ProcessResult style = runGradle(repoPath, ":" + taskId + ":checkstyleMain");
        result.setStyleOutput(style.getOutput());
        result.setStyleStatus(resolveStatus(style));

        // Step 3: tests (only if step 2 passed; NOT_AVAILABLE is not a failure)
        boolean step2Passed = isPassedOrNa(result.getDocsStatus())
                && isPassedOrNa(result.getStyleStatus());

        if (!step2Passed) {
            log.info("[" + github + "/" + taskId + "] Step 3 skipped: docs/style failed");
            return result;
        }

        log.info("[" + github + "/" + taskId + "] Step 3: tests");
        ProcessResult tests = runGradle(repoPath, ":" + taskId + ":test", "--continue");
        result.setTestOutput(tests.getOutput());

        if (tests.isTimedOut()) {
            result.setTestStatus(BuildStatus.TIMEOUT);
        } else {
            TestCounts counts = parseTestResults(repoPath, taskId);
            result.setTestCounts(counts);
            result.setTestStatus(counts.getFailed() > 0 ? BuildStatus.FAILED : BuildStatus.SUCCESS);
        }

        return result;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private BuildStatus resolveStatus(ProcessResult pr) {
        if (pr.isTimedOut()) {
            return BuildStatus.TIMEOUT;
        }
        if (isTaskNotFound(pr.getOutput())) {
            return BuildStatus.NOT_AVAILABLE;
        }
        return pr.isSuccess() ? BuildStatus.SUCCESS : BuildStatus.FAILED;
    }

    private boolean isPassedOrNa(BuildStatus status) {
        return status == BuildStatus.SUCCESS || status == BuildStatus.NOT_AVAILABLE;
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
        return (output.contains("Task '") && output.contains("' not found"))
                || (output.contains("Could not find") && output.contains("task"));
    }

    /** Parses JUnit XML files from build/test-results and aggregates counts. */
    private TestCounts parseTestResults(Path repoPath, String taskId) {
        TestCounts total = new TestCounts();
        Path dir = repoPath.resolve(taskId).resolve("build").resolve("test-results");

        if (!Files.exists(dir)) {
            return total;
        }

        try (Stream<Path> stream = Files.walk(dir)) {
            stream.filter(p -> p.toString().endsWith(".xml"))
                    .forEach(xml -> {
                        try {
                            total.add(parseXmlFile(xml.toFile()));
                        } catch (Exception e) {
                            log.warning("Could not parse " + xml + ": " + e.getMessage());
                        }
                    });
        } catch (IOException e) {
            log.warning("Could not walk " + dir + ": " + e.getMessage());
        }
        return total;
    }

    private TestCounts parseXmlFile(File xmlFile) throws Exception {
        Document doc = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder().parse(xmlFile);

        NodeList suites = doc.getElementsByTagName("testsuite");
        int passed = 0;
        int failed = 0;
        int skipped = 0;

        for (int i = 0; i < suites.getLength(); i++) {
            Element suite  = (Element) suites.item(i);
            int tests      = intAttr(suite, "tests");
            int failures   = intAttr(suite, "failures");
            int errors     = intAttr(suite, "errors");
            int skip       = intAttr(suite, "skipped");
            failed  += failures + errors;
            skipped += skip;
            passed  += tests - failures - errors - skip;
        }
        return new TestCounts(Math.max(0, passed), failed, skipped);
    }

    private int intAttr(Element el, String attr) {
        String v = el.getAttribute(attr);
        try {
            return v != null && !v.isEmpty() ? Integer.parseInt(v) : 0;
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}

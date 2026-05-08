package ru.nsu.krasnyanskii.checker;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
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
 *
 * <p>Pipeline per task:
 * (1) compile &rarr; (2) javadoc + checkstyle &rarr; (3) tests.
 * Each step is skipped when the previous step failed.</p>
 *
 * <p>Students are checked in parallel via {@code parallelStream()}.</p>
 */
public class ProjectChecker {

    private final OopCheckerConfig config;
    private final GitManager       gitManager;
    private final ScoreCalculator  scoreCalc;
    private final ActivityTracker  activityTracker;
    private final ProcessRunner    processRunner;
    private final CheckerView      view;

    /**
     * Creates a ProjectChecker.
     *
     * @param config   parsed OOP checker configuration
     * @param reposDir directory where student repos will be cloned
     * @param view     view for all console output
     */
    public ProjectChecker(OopCheckerConfig config, Path reposDir, CheckerView view) {
        this.config          = config;
        this.view            = view;
        int timeout          = config.getScoringConfig().getTestTimeoutSeconds();
        this.gitManager      = new GitManager(reposDir, timeout, view);
        this.scoreCalc       = new ScoreCalculator(config);
        this.activityTracker = new ActivityTracker(gitManager);
        this.processRunner   = new ProcessRunner(timeout);
    }

    /**
     * Runs checks for all students listed in the config's check instruction.
     * Students are processed in parallel.
     *
     * @return list of per-student check results
     */
    public List<StudentCheckResult> runChecks() {
        CheckInstruction instruction = config.getCheckInstruction();
        return instruction.getStudentGithubs().parallelStream()
                .map(this::checkStudent)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private StudentCheckResult checkStudent(String github) {
        Optional<Student> studentOpt = config.findStudentByGithub(github);
        if (studentOpt.isEmpty()) {
            view.warnStudentNotFound(github);
            return null;
        }

        Student student   = studentOpt.get();
        String  groupName = config.findGroupByStudentGithub(github)
                .map(Group::getName).orElse("Unknown");

        StudentCheckResult studentResult =
                new StudentCheckResult(github, student.getFullName(), groupName);

        Path repoPath;
        try {
            repoPath = gitManager.cloneOrUpdate(github, student.getRepoUrl());
        } catch (Exception e) {
            view.errorCloneFailed(github, e.getMessage());
            return studentResult;
        }

        CheckInstruction instruction = config.getCheckInstruction();
        for (String taskId : instruction.getTaskIds()) {
            TaskCheckResult taskResult = checkTask(repoPath, github, taskId);
            taskResult.setScore(scoreCalc.calculate(github, taskResult));
            studentResult.addTaskResult(taskResult);
        }

        addActivityBonus(repoPath, studentResult);
        return studentResult;
    }

    private void addActivityBonus(Path repoPath, StudentCheckResult studentResult) {
        ActivityConfig activityConfig = config.getActivityConfig();
        if (activityConfig != null) {
            int    activeWeeks   = activityTracker.countActiveWeeks(repoPath, activityConfig);
            double activityBonus = activityTracker.calculateActivityBonus(
                    activeWeeks, activityConfig);
            studentResult.setActiveWeeks(activeWeeks);
            studentResult.setActivityBonus(activityBonus);
        }
    }

    private TaskCheckResult checkTask(Path repoPath, String github, String taskId) {
        TaskCheckResult result = new TaskCheckResult(taskId);
        result.setLastCommitDate(gitManager.getLastCommitDate(repoPath, taskId));

        if (!runCompileStep(repoPath, github, taskId, result)) {
            return result;
        }
        if (!runDocsAndStyleStep(repoPath, github, taskId, result)) {
            return result;
        }
        runTestStep(repoPath, github, taskId, result);
        return result;
    }

    /**
     * Runs the compile step.
     *
     * @return true if compilation succeeded; false otherwise (caller must stop pipeline)
     */
    private boolean runCompileStep(Path repoPath, String github,
                                   String taskId, TaskCheckResult result) {
        view.infoStep(github, taskId, "Step 1: compile");
        ProcessResult compile = runTaskGradle(repoPath, taskId, "compileJava");
        result.setCompileOutput(compile.getOutput());
        result.setCompileStatus(resolveStatus(compile));
        return result.getCompileStatus() == BuildStatus.SUCCESS;
    }

    /**
     * Runs the javadoc and checkstyle steps.
     *
     * @return true if both docs and style passed (or were unavailable); false otherwise
     */
    private boolean runDocsAndStyleStep(Path repoPath, String github,
                                        String taskId, TaskCheckResult result) {
        view.infoStep(github, taskId, "Step 2: javadoc");
        ProcessResult docs = runTaskGradle(repoPath, taskId, "javadoc");
        result.setDocsOutput(docs.getOutput());
        result.setDocsStatus(resolveStatus(docs));

        view.infoStep(github, taskId, "Step 2: checkstyle");
        ProcessResult style = runTaskGradle(repoPath, taskId, "checkstyleMain");
        result.setStyleOutput(style.getOutput());
        result.setStyleStatus(resolveStatus(style));

        boolean passed = isPassedOrNa(result.getDocsStatus())
                && isPassedOrNa(result.getStyleStatus());
        if (!passed) {
            view.infoStep(github, taskId, "Step 3 skipped: docs/style failed");
        }
        return passed;
    }

    /**
     * Runs the test step and records counts or timeout status.
     */
    private void runTestStep(Path repoPath, String github,
                             String taskId, TaskCheckResult result) {
        view.infoStep(github, taskId, "Step 3: tests");
        ProcessResult tests = runTaskGradle(repoPath, taskId, "test", "--continue");
        result.setTestOutput(tests.getOutput());

        if (tests.isTimedOut()) {
            result.setTestStatus(BuildStatus.TIMEOUT);
        } else {
            TestCounts counts = parseTestResults(repoPath, taskId);
            result.setTestCounts(counts);
            result.setTestStatus(
                    counts.getFailed() > 0 ? BuildStatus.FAILED : BuildStatus.SUCCESS);
        }
    }

    /**
     * Runs a Gradle task for a specific task subdirectory.
     *
     * <p>If the subdirectory contains its own {@code gradlew} wrapper (standalone project),
     * the wrapper is invoked directly from inside that directory.
     * Otherwise the root wrapper is used with the multi-project notation
     * {@code :taskId:gradleTask}.</p>
     */
    private ProcessResult runTaskGradle(Path repoPath, String taskId,
                                        String gradleTask, String... extra) {
        Path taskDir = repoPath.resolve(taskId);
        boolean standalone = Files.exists(taskDir.resolve("gradlew"))
                || Files.exists(taskDir.resolve("gradlew.bat"));

        if (standalone) {
            String[] args = new String[1 + extra.length];
            args[0] = gradleTask;
            System.arraycopy(extra, 0, args, 1, extra.length);
            return runGradle(taskDir, args);
        }

        String[] args = new String[1 + extra.length];
        args[0] = ":" + taskId + ":" + gradleTask;
        System.arraycopy(extra, 0, args, 1, extra.length);
        return runGradle(repoPath, args);
    }

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

    private ProcessResult runGradle(Path repoPath, String... args) {
        List<String> cmd = buildGradleCommand(repoPath, args);
        try {
            return processRunner.run(repoPath, cmd);
        } catch (IOException | InterruptedException e) {
            return new ProcessResult(-1, e.getMessage(), false);
        }
    }

    /**
     * Builds the command list for invoking gradlew.
     * On Windows, batch files (.bat) cannot be started directly via ProcessBuilder —
     * they need to go through cmd.exe.
     */
    private List<String> buildGradleCommand(Path repoPath, String[] args) {
        List<String> cmd = new ArrayList<>();
        boolean isWin = System.getProperty("os.name", "").toLowerCase().contains("win");
        if (isWin) {
            cmd.add("cmd");
            cmd.add("/c");
            cmd.add(repoPath.resolve("gradlew.bat").toAbsolutePath().toString());
        } else {
            cmd.add(repoPath.resolve("gradlew").toAbsolutePath().toString());
        }
        cmd.addAll(Arrays.asList(args));
        return cmd;
    }

    private boolean isTaskNotFound(String output) {
        return (output.contains("Task '") && output.contains("' not found"))
                || (output.contains("Could not find") && output.contains("task"));
    }

    /**
     * Parses JUnit XML files from {@code build/test-results} and aggregates counts.
     */
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
                            view.warnXmlParseFailed(xml.toString(), e.getMessage());
                        }
                    });
        } catch (IOException e) {
            view.warnWalkFailed(dir.toString(), e.getMessage());
        }
        return total;
    }

    private TestCounts parseXmlFile(File xmlFile) throws Exception {
        Document doc = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder().parse(xmlFile);

        NodeList suites = doc.getElementsByTagName("testsuite");
        int passed  = 0;
        int failed  = 0;
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

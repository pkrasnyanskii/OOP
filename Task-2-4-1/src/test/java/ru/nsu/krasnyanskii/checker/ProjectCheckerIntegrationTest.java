package ru.nsu.krasnyanskii.checker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.nsu.krasnyanskii.model.CheckInstruction;
import ru.nsu.krasnyanskii.model.Group;
import ru.nsu.krasnyanskii.model.OopCheckerConfig;
import ru.nsu.krasnyanskii.model.Student;
import ru.nsu.krasnyanskii.model.Task;
import ru.nsu.krasnyanskii.model.results.BuildStatus;
import ru.nsu.krasnyanskii.model.results.StudentCheckResult;
import ru.nsu.krasnyanskii.model.results.TaskCheckResult;
import ru.nsu.krasnyanskii.report.HtmlReporter;

/**
 * Integration tests: clones a real public repository and runs the full check pipeline.
 *
 * <p>Requires network access and a working git installation.
 * Excluded from the regular {@code test} task; run separately:</p>
 * <pre>./gradlew integrationTest</pre>
 *
 * <p>After a successful run the HTML report is saved to
 * {@code build/reports/integration/report.html} and a summary is printed to stdout.</p>
 */
@Tag("integration")
@DisplayName("ProjectChecker — full pipeline against a real repository")
class ProjectCheckerIntegrationTest {

    private static final String GITHUB   = "pkrasnyanskii";
    private static final String REPO_URL = "https://github.com/pkrasnyanskii/OOP";

    private static final String TASK_1_1   = "Task-1-1";
    private static final String TASK_1_2   = "Task-1-2";
    private static final String TASK_1_3   = "Task-1-3";
    private static final String TASK_1_5_1 = "Task-1-5-1";

    private static final Path REPORT_DIR =
            Paths.get("build", "reports", "integration");

    @Test
    @DisplayName("Semester-1 tasks compile, tests pass, grade >= 3")
    void semester1Tasks() throws Exception {
        Files.createDirectories(REPORT_DIR);

        List<String> tasks = List.of(TASK_1_1, TASK_1_2, TASK_1_3, TASK_1_5_1);
        OopCheckerConfig config = buildConfig(tasks);

        Path logFile  = REPORT_DIR.resolve("checker.log");
        Path htmlFile = REPORT_DIR.resolve("report.html");

        CheckerView view = new CheckerView(
                new PrintStream(Files.newOutputStream(logFile)),
                System.out);

        Path reposDir = REPORT_DIR.resolve("repos");
        ProjectChecker checker = new ProjectChecker(config, reposDir, view);

        List<StudentCheckResult> results = checker.runChecks();

        String html = new HtmlReporter(config).generate(results);
        Files.writeString(htmlFile, html);

        printSummary(results, tasks, config);

        System.out.println("HTML report: " + htmlFile.toAbsolutePath());
        System.out.println("Checker log: " + logFile.toAbsolutePath());

        assertFalse(results.isEmpty(), "Expected at least one student result");

        StudentCheckResult sr = results.get(0);
        assertEquals(GITHUB, sr.getStudentGithub());

        for (String taskId : tasks) {
            assertTaskPasses(sr, taskId, config);
        }
    }

    private void printSummary(List<StudentCheckResult> results,
                              List<String> tasks,
                              OopCheckerConfig config) {
        System.out.println();
        System.out.println("==============================");
        System.out.println("  Integration Test Summary");
        System.out.println("==============================");

        for (StudentCheckResult sr : results) {
            System.out.printf("Student : %s (@%s)%n",
                    sr.getStudentName(), sr.getStudentGithub());
            System.out.printf("Group   : %s%n", sr.getGroupName());
            System.out.println("------------------------------");

            for (String taskId : tasks) {
                TaskCheckResult tr = sr.getTaskResult(taskId).orElse(null);
                if (tr == null) {
                    System.out.printf("  %-12s  NO RESULT%n", taskId);
                    continue;
                }

                String compile = statusIcon(tr.getCompileStatus());
                String tests   = tr.getTestCounts() != null
                        ? tr.getTestCounts().getPassed() + "/"
                          + (tr.getTestCounts().getPassed() + tr.getTestCounts().getFailed())
                          + " passed"
                        : "—";
                double maxScore = config.findTaskById(taskId)
                        .map(Task::getMaxScore).orElse(100.0);

                System.out.printf("  %-12s  compile: %s  tests: %-14s  score: %.1f/%.0f%n",
                        taskId, compile, tests, tr.getScore(), maxScore);
            }

            System.out.println("------------------------------");
            System.out.printf("  Total score : %.1f%n", sr.getTotalScore());
            System.out.println("==============================");
        }
    }

    private String statusIcon(BuildStatus status) {
        return switch (status) {
            case SUCCESS       -> "OK ";
            case FAILED        -> "ERR";
            case TIMEOUT       -> "TMO";
            case NOT_AVAILABLE -> "N/A";
            case NOT_CHECKED   -> " ? ";
        };
    }

    /**
     * Asserts that a task did not fail to compile, all tests pass,
     * and the score is at least 10% of max (satisfactory threshold).
     *
     * <p>A compile status of {@code NOT_AVAILABLE} means the task is not registered
     * as a Gradle sub-project in the checked repository, which is treated as a pass
     * for the purpose of this integration check.</p>
     */
    private void assertTaskPasses(StudentCheckResult sr, String taskId, OopCheckerConfig config) {
        TaskCheckResult taskResult = sr.getTaskResult(taskId)
                .orElseThrow(() -> new AssertionError("No result for " + taskId));

        BuildStatus compileStatus = taskResult.getCompileStatus();
        assertTrue(
                compileStatus == BuildStatus.SUCCESS
                        || compileStatus == BuildStatus.NOT_AVAILABLE,
                taskId + " compile status should be SUCCESS or NOT_AVAILABLE, was: "
                        + compileStatus);

        if (compileStatus == BuildStatus.NOT_AVAILABLE) {
            return;
        }

        assertNotNull(taskResult.getTestCounts(), taskId + ": test counts should be populated");
        assertEquals(0, taskResult.getTestCounts().getFailed(),
                taskId + ": no failing tests expected");

        double score    = taskResult.getScore();
        double maxScore = config.findTaskById(taskId).map(Task::getMaxScore).orElse(100.0);
        double pct      = maxScore > 0 ? score / maxScore * 100 : 0;
        String msg = taskId + ": score " + score + "/" + maxScore + " = " + pct + "% < 10%";
        assertTrue(pct >= 10.0, msg);
    }

    private OopCheckerConfig buildConfig(List<String> taskIds) {
        OopCheckerConfig config = new OopCheckerConfig();

        for (String id : taskIds) {
            config.addTask(new Task(id, id, 100.0, null, null));
        }

        config.getScoringConfig().getGradeScale().setSatisfactory(10.0);

        Student student = new Student(GITHUB, "Krasnyansky Pyotr", REPO_URL);
        Group group = new Group("24214");
        group.addStudent(student);
        config.addGroup(group);

        CheckInstruction instruction = new CheckInstruction();
        instruction.getStudentGithubs().add(GITHUB);
        instruction.getTaskIds().addAll(taskIds);
        config.setCheckInstruction(instruction);

        return config;
    }
}

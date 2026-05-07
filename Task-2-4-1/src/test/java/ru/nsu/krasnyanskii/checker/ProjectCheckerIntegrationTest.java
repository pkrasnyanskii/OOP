package ru.nsu.krasnyanskii.checker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ru.nsu.krasnyanskii.model.CheckInstruction;
import ru.nsu.krasnyanskii.model.Group;
import ru.nsu.krasnyanskii.model.OopCheckerConfig;
import ru.nsu.krasnyanskii.model.Student;
import ru.nsu.krasnyanskii.model.Task;
import ru.nsu.krasnyanskii.model.results.BuildStatus;
import ru.nsu.krasnyanskii.model.results.StudentCheckResult;
import ru.nsu.krasnyanskii.model.results.TaskCheckResult;

/**
 * Integration tests: clones a real public repository and runs the full check pipeline.
 *
 * <p>Requires network access and a working git installation.
 * Excluded from the regular {@code test} task; run separately:</p>
 * <pre>./gradlew integrationTest</pre>
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

    @Test
    @DisplayName("Semester-1 tasks compile, tests pass, grade >= 3")
    void semester1Tasks(@TempDir Path tempDir) throws Exception {
        List<String> tasks = List.of(TASK_1_1, TASK_1_2, TASK_1_3, TASK_1_5_1);
        OopCheckerConfig config = buildConfig(tasks);

        CheckerView view = new CheckerView(
                new PrintStream(Files.newOutputStream(tempDir.resolve("checker.log"))),
                new PrintStream(Files.newOutputStream(tempDir.resolve("report.html"))),
                false);

        Path reposDir = tempDir.resolve("repos");
        ProjectChecker checker = new ProjectChecker(config, reposDir, view);

        List<StudentCheckResult> results = checker.runChecks();

        assertFalse(results.isEmpty(), "Expected at least one student result");

        StudentCheckResult sr = results.get(0);
        assertEquals(GITHUB, sr.getStudentGithub());

        for (String taskId : tasks) {
            assertTaskPasses(sr, taskId, config);
        }
    }

    /**
     * Asserts that a task compiled successfully, all tests pass,
     * and the score is at least 10% of max (satisfactory threshold).
     */
    private void assertTaskPasses(StudentCheckResult sr, String taskId, OopCheckerConfig config) {
        TaskCheckResult taskResult = sr.getTaskResult(taskId)
                .orElseThrow(() -> new AssertionError("No result for " + taskId));

        assertEquals(BuildStatus.SUCCESS, taskResult.getCompileStatus(),
                taskId + " should compile successfully");
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

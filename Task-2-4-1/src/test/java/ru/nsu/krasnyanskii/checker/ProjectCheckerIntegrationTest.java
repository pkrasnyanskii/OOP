package ru.nsu.krasnyanskii.checker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
 * <p>These tests require network access and a working git installation.
 * They are excluded from the regular {@code test} task and can be run separately:</p>
 * <pre>./gradlew integrationTest</pre>
 */
@Tag("integration")
@DisplayName("ProjectChecker — integration against real repository")
class ProjectCheckerIntegrationTest {

    private static final String GITHUB   = "pkrasnyanskii";
    private static final String REPO_URL = "https://github.com/pkrasnyanskii/OOP";
    private static final String TASK_ID  = "Task-1-1";

    @Test
    @DisplayName("Task-1-1 (HeapSort) compiles and tests pass in pkrasnyanskii/OOP")
    void task11CompilesAndTestsPass(@TempDir Path tempDir) throws Exception {
        OopCheckerConfig config = buildConfig();

        // Use a silent view so integration test output is clean
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

        TaskCheckResult taskResult = sr.getTaskResult(TASK_ID)
                .orElseThrow(() -> new AssertionError("No result for " + TASK_ID));

        assertEquals(BuildStatus.SUCCESS, taskResult.getCompileStatus(),
                "Task-1-1 should compile successfully");
        assertNotNull(taskResult.getTestCounts(),
                "Test counts should be populated");
        assertEquals(0, taskResult.getTestCounts().getFailed(),
                "Task-1-1 should have no failing tests");
    }

    private OopCheckerConfig buildConfig() {
        OopCheckerConfig config = new OopCheckerConfig();

        // Task under test
        Task task = new Task(TASK_ID, "Сортировка кучей", 100.0, null, null);
        config.addTask(task);

        // Student: real GitHub account
        Student student = new Student(GITHUB, "Красняnskий Пётр", REPO_URL);
        Group group = new Group("TEST");
        group.addStudent(student);
        config.addGroup(group);

        // Check instruction
        CheckInstruction instruction = new CheckInstruction();
        instruction.getStudentGithubs().add(GITHUB);
        instruction.getTaskIds().add(TASK_ID);
        config.setCheckInstruction(instruction);

        return config;
    }
}

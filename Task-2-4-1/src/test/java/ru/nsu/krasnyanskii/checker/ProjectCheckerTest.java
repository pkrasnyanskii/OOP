package ru.nsu.krasnyanskii.checker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

@DisplayName("ProjectChecker — оркестрация проверок")
class ProjectCheckerTest {

    private static final String GITHUB  = "test-student";
    private static final String TASK_ID = "Task-X";

    private CheckerView view;

    @BeforeEach
    void setUp() {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        view = new CheckerView(new PrintStream(buf), new PrintStream(buf), false);
    }

    private OopCheckerConfig buildConfig(String github, String repoUrl, String taskId) {
        OopCheckerConfig config = new OopCheckerConfig();
        config.addTask(new Task(taskId, "Test task", 100.0, null, null));

        Student student = new Student(github, "Test Student", repoUrl);
        Group   group   = new Group("test-group");
        group.addStudent(student);
        config.addGroup(group);

        CheckInstruction instruction = new CheckInstruction();
        instruction.getStudentGithubs().add(github);
        instruction.getTaskIds().add(taskId);
        config.setCheckInstruction(instruction);
        return config;
    }

    private void createFakeGradlew(Path repoDir, int exitCode) throws IOException {
        boolean isWin = System.getProperty("os.name", "").toLowerCase().contains("win");
        if (isWin) {
            Path bat = repoDir.resolve("gradlew.bat");
            Files.write(bat, ("@echo off\r\nexit /b " + exitCode + "\r\n")
                    .getBytes(StandardCharsets.UTF_8));
        } else {
            Path sh = repoDir.resolve("gradlew");
            Files.write(sh, ("#!/bin/sh\nexit " + exitCode + "\n")
                    .getBytes(StandardCharsets.UTF_8));
            sh.toFile().setExecutable(true);
        }
    }

    private void initGitRepo(Path repoDir) throws Exception {
        ProcessRunner runner = new ProcessRunner(30);
        runner.run(repoDir, "git", "init");
        runner.run(repoDir, "git", "-c", "user.email=t@t.com",
                "-c", "user.name=T", "commit", "--allow-empty", "-m", "init");
    }

    @Test
    @DisplayName("Студент не найден в конфиге → пустой список результатов")
    void studentNotInConfig(@TempDir Path reposDir) {
        OopCheckerConfig config = new OopCheckerConfig();
        config.addTask(new Task(TASK_ID, "T", 100.0, null, null));

        CheckInstruction instruction = new CheckInstruction();
        instruction.getStudentGithubs().add("unknown-user");
        instruction.getTaskIds().add(TASK_ID);
        config.setCheckInstruction(instruction);

        ProjectChecker checker = new ProjectChecker(config, reposDir, view);
        List<StudentCheckResult> results = checker.runChecks();

        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Клонирование по несуществующему URL → результат без задач")
    void cloneFails(@TempDir Path reposDir) {
        OopCheckerConfig config = buildConfig(
                GITHUB, "https://github.com/nonexistent-xyz/no-such-repo", TASK_ID);

        ProjectChecker checker = new ProjectChecker(config, reposDir, view);
        List<StudentCheckResult> results = checker.runChecks();

        assertFalse(results.isEmpty());
        StudentCheckResult sr = results.get(0);
        assertEquals(GITHUB, sr.getStudentGithub());
        assertTrue(sr.getTaskResults().isEmpty());
    }

    @Test
    @DisplayName("Успешный pipeline: компиляция проходит, тесты проходят")
    void fullPipelineSuccess(@TempDir Path reposDir) throws Exception {
        Path repoDir = reposDir.resolve(GITHUB);
        Files.createDirectories(repoDir);
        initGitRepo(repoDir);
        createFakeGradlew(repoDir, 0);

        OopCheckerConfig config = buildConfig(
                GITHUB, "https://example.com/fake", TASK_ID);

        ProjectChecker checker = new ProjectChecker(config, reposDir, view);
        List<StudentCheckResult> results = checker.runChecks();

        assertFalse(results.isEmpty());
        StudentCheckResult sr = results.get(0);
        assertTrue(sr.getTaskResult(TASK_ID).isPresent());

        TaskCheckResult tr = sr.getTaskResult(TASK_ID).get();
        assertEquals(BuildStatus.SUCCESS, tr.getCompileStatus());
        assertEquals(BuildStatus.SUCCESS, tr.getTestStatus());
        assertNotNull(tr.getTestCounts());
        assertEquals(0, tr.getTestCounts().getFailed());
    }

    @Test
    @DisplayName("Компиляция падает → pipeline останавливается, тесты не запускаются")
    void compileFailed(@TempDir Path reposDir) throws Exception {
        Path repoDir = reposDir.resolve(GITHUB);
        Files.createDirectories(repoDir);
        initGitRepo(repoDir);
        createFakeGradlew(repoDir, 1);

        OopCheckerConfig config = buildConfig(
                GITHUB, "https://example.com/fake", TASK_ID);

        ProjectChecker checker = new ProjectChecker(config, reposDir, view);
        List<StudentCheckResult> results = checker.runChecks();

        assertFalse(results.isEmpty());
        StudentCheckResult sr = results.get(0);
        assertTrue(sr.getTaskResult(TASK_ID).isPresent());

        TaskCheckResult tr = sr.getTaskResult(TASK_ID).get();
        assertEquals(BuildStatus.FAILED, tr.getCompileStatus());
        assertEquals(BuildStatus.NOT_CHECKED, tr.getTestStatus());
    }

    @Test
    @DisplayName("parseTestResults с корректным XML считает тесты")
    void parseTestResultsWithXml(@TempDir Path reposDir) throws Exception {
        Path repoDir = reposDir.resolve(GITHUB);
        Files.createDirectories(repoDir);
        initGitRepo(repoDir);
        createFakeGradlew(repoDir, 0);

        Path xmlDir = repoDir.resolve(TASK_ID).resolve("build").resolve("test-results").resolve("test");
        Files.createDirectories(xmlDir);
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<testsuite name=\"MyTest\" tests=\"3\" failures=\"1\" errors=\"0\" skipped=\"0\">"
                + "<testcase name=\"test1\"/>"
                + "<testcase name=\"test2\"><failure message=\"fail\"/></testcase>"
                + "<testcase name=\"test3\"/>"
                + "</testsuite>";
        Files.write(xmlDir.resolve("TEST-MyTest.xml"), xml.getBytes(StandardCharsets.UTF_8));

        OopCheckerConfig config = buildConfig(
                GITHUB, "https://example.com/fake", TASK_ID);

        ProjectChecker checker = new ProjectChecker(config, reposDir, view);
        List<StudentCheckResult> results = checker.runChecks();

        assertFalse(results.isEmpty());
        StudentCheckResult sr = results.get(0);
        assertTrue(sr.getTaskResult(TASK_ID).isPresent());

        TaskCheckResult tr = sr.getTaskResult(TASK_ID).get();
        assertEquals(BuildStatus.SUCCESS, tr.getCompileStatus());
        assertNotNull(tr.getTestCounts());
        assertEquals(1, tr.getTestCounts().getFailed());
        assertEquals(2, tr.getTestCounts().getPassed());
    }
}

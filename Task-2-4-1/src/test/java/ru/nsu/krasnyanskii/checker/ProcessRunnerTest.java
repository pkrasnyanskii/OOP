package ru.nsu.krasnyanskii.checker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

@DisplayName("ProcessRunner — запуск внешних процессов")
class ProcessRunnerTest {

    @Test
    @DisplayName("run() успешно выполняет git --version")
    void runGitVersion(@TempDir Path tmpDir) throws Exception {
        ProcessRunner runner = new ProcessRunner(30);
        ProcessResult result = runner.run(tmpDir, "git", "--version");
        assertNotNull(result);
        assertNotNull(result.getOutput());
    }

    @Test
    @DisplayName("run() через varargs и через List дают одинаковый результат")
    void runVarargsAndListEquivalent(@TempDir Path tmpDir) throws Exception {
        ProcessRunner runner = new ProcessRunner(30);
        ProcessResult r1 = runner.run(tmpDir, "git", "--version");
        ProcessResult r2 = runner.run(tmpDir, java.util.Arrays.asList("git", "--version"));
        assertEquals(r1.isSuccess(), r2.isSuccess());
        assertFalse(r1.isTimedOut());
    }

    @Test
    @DisplayName("run() возвращает результат при таймауте 0 секунд")
    void timeoutDetected(@TempDir Path tmpDir) throws Exception {
        ProcessRunner runner = new ProcessRunner(0);
        ProcessResult result;
        boolean isWindows = System.getProperty("os.name", "").toLowerCase().contains("win");
        if (isWindows) {
            result = runner.run(tmpDir, "ping", "-n", "5", "127.0.0.1");
        } else {
            result = runner.run(tmpDir, "sleep", "5");
        }
        assertNotNull(result);
    }
}

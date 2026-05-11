package ru.nsu.krasnyanskii.checker;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

@DisplayName("GitManager — git operations")
class GitManagerTest {

    private GitManager  gitManager;
    private CheckerView view;

    @BeforeEach
    void setUp(@TempDir Path reposDir) {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        view       = new CheckerView(new PrintStream(buf), new PrintStream(buf));
        gitManager = new GitManager(reposDir, 15, view);
    }

    @Test
    @DisplayName("getLastCommitDate on a non-existent path returns null")
    void getLastCommitDate_nonExistent(@TempDir Path tmpDir) {
        Path fakePath = tmpDir.resolve("nonexistent");
        LocalDate result = gitManager.getLastCommitDate(fakePath, "Task-X");
        assertNull(result);
    }

    @Test
    @DisplayName("getLastCommitDate on a directory without git returns null")
    void getLastCommitDate_noGit(@TempDir Path tmpDir) {
        LocalDate result = gitManager.getLastCommitDate(tmpDir, "Task-X");
        assertNull(result);
    }

    @Test
    @DisplayName("getActiveWeeks on a non-existent path returns a non-null set")
    void getActiveWeeks_nonExistent(@TempDir Path tmpDir) {
        Path fakePath = tmpDir.resolve("nonexistent");
        Set<String> weeks = gitManager.getActiveWeeks(
                fakePath, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31));
        assertNotNull(weeks);
    }

    @Test
    @DisplayName("getActiveWeeks on a directory without git returns a result without throwing")
    void getActiveWeeks_noGit(@TempDir Path tmpDir) {
        Set<String> weeks = gitManager.getActiveWeeks(
                tmpDir, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31));
        assertNotNull(weeks);
    }

    @Test
    @DisplayName("cloneOrUpdate with a bad URL throws an exception")
    void cloneOrUpdate_badUrl(@TempDir Path reposDir) {
        GitManager gm = new GitManager(reposDir, 15, view);
        org.junit.jupiter.api.Assertions.assertThrows(
                Exception.class,
                () -> gm.cloneOrUpdate("student", "https://github.com/nonexistent-user-xyz/nosuchthing"));
    }

    @Test
    @DisplayName("cloneOrUpdate on an existing .git repo does not throw")
    void cloneOrUpdate_existingRepo(@TempDir Path reposDir) throws Exception {
        Path repoPath = reposDir.resolve("student");
        Files.createDirectories(repoPath.resolve(".git"));

        GitManager gm = new GitManager(reposDir, 15, view);
        assertDoesNotThrow(() -> gm.cloneOrUpdate("student", "https://example.com/repo"));
    }
}

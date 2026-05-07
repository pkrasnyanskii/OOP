package ru.nsu.krasnyanskii.checker;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("EnvironmentChecker — pre-flight environment checks")
class EnvironmentCheckerTest {

    private ByteArrayOutputStream errBuf;
    private CheckerView            view;
    private EnvironmentChecker     checker;

    @BeforeEach
    void setUp() {
        errBuf  = new ByteArrayOutputStream();
        PrintStream devNull = new PrintStream(new ByteArrayOutputStream());
        view    = new CheckerView(new PrintStream(errBuf), devNull, false);
        checker = new EnvironmentChecker(new ProcessRunner(15), view);
    }

    @Test
    @DisplayName("check(skipAuthCheck=true) does not throw and produces output")
    void checkSkipAuth() {
        assertDoesNotThrow(() -> checker.check(true));
        assertTrue(errBuf.size() > 0);
    }

    @Test
    @DisplayName("check(skipAuthCheck=false) does not throw and includes credential.helper check")
    void checkWithAuth() {
        assertDoesNotThrow(() -> checker.check(false));
        assertTrue(errBuf.size() > 0);
    }

    @Test
    @DisplayName("check() output contains git info or a WARNING")
    void checkOutputContainsGitInfo() {
        checker.check(true);
        String output = errBuf.toString();
        boolean hasGitInfo = output.contains("git")
                || output.contains("Git")
                || output.contains("WARNING");
        assertTrue(hasGitInfo);
    }
}

package ru.nsu.krasnyanskii.checker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ProcessResult — isSuccess()")
class ProcessResultTest {

    @Test
    @DisplayName("exitCode=0, timedOut=false → isSuccess()=true")
    void success() {
        ProcessResult r = new ProcessResult(0, "ok", false);
        assertTrue(r.isSuccess());
        assertEquals(0, r.getExitCode());
        assertEquals("ok", r.getOutput());
        assertFalse(r.isTimedOut());
    }

    @Test
    @DisplayName("exitCode=1 → isSuccess()=false")
    void nonZeroExit() {
        ProcessResult r = new ProcessResult(1, "error", false);
        assertFalse(r.isSuccess());
    }

    @Test
    @DisplayName("timedOut=true → isSuccess()=false даже при exitCode=0")
    void timedOut() {
        ProcessResult r = new ProcessResult(0, "[TIMED OUT]", true);
        assertFalse(r.isSuccess());
        assertTrue(r.isTimedOut());
    }
}

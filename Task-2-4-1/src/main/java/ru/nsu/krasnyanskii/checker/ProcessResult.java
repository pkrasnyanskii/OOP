package ru.nsu.krasnyanskii.checker;

import lombok.Value;

/** Immutable result of an external process execution. */
@Value
public class ProcessResult {
    int     exitCode;
    String  output;
    boolean timedOut;

    /**
     * Returns true if the process exited with code 0 and did not time out.
     *
     * @return true on success
     */
    public boolean isSuccess() {
        return !timedOut && exitCode == 0;
    }
}

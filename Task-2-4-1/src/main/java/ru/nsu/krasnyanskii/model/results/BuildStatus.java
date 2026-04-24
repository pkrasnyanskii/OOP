package ru.nsu.krasnyanskii.model.results;

/** Result of a single build pipeline step. */
public enum BuildStatus {
    NOT_CHECKED,
    SUCCESS,
    FAILED,
    TIMEOUT,
    NOT_AVAILABLE
}

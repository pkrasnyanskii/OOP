package ru.nsu.krasnyanskii.model;

import lombok.Value;

/** Immutable bonus points record for a specific student and task. */
@Value
public class BonusEntry {
    String studentGithub;
    String taskId;
    double points;
}

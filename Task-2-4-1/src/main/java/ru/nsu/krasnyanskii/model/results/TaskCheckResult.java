package ru.nsu.krasnyanskii.model.results;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

/**
 * Check result for a single task of a single student.
 * Pipeline order: compile → docs + style → tests (each step blocked by the previous).
 */
@Getter
@Setter
public class TaskCheckResult {
    private final String taskId;

    private BuildStatus compileStatus = BuildStatus.NOT_CHECKED;
    private BuildStatus docsStatus    = BuildStatus.NOT_CHECKED;
    private BuildStatus styleStatus   = BuildStatus.NOT_CHECKED;
    private BuildStatus testStatus    = BuildStatus.NOT_CHECKED;

    private TestCounts testCounts    = new TestCounts();
    private LocalDate  lastCommitDate;
    private double     score          = 0.0;

    private String compileOutput = "";
    private String docsOutput    = "";
    private String styleOutput   = "";
    private String testOutput    = "";

    public TaskCheckResult(String taskId) {
        this.taskId = taskId;
    }
}

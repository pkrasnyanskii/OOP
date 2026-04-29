package ru.nsu.krasnyanskii.model;

import lombok.Data;

/** Parameters controlling how scores are calculated. */
@Data
public class ScoringConfig {
    private int        testTimeoutSeconds        = 300;
    private double     styleDeductionPercent     = 10.0;
    private double     docsDeductionPercent      = 10.0;
    private double     softDeadlinePenaltyPerDay = 1.0;
    private GradeScale gradeScale                = new GradeScale();
}

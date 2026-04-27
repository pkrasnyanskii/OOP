package org.example.model;

import lombok.Data;

/**
 * Параметры системы выставления баллов.
 *
 * testTimeoutSeconds        — максимальное время выполнения тестов (секунды)
 * styleDeductionPercent     — штраф за нарушение стиля (% от maxScore задачи)
 * docsDeductionPercent      — штраф за проблемы с документацией (% от maxScore)
 * softDeadlinePenaltyPerDay — штраф в баллах за каждый день после softDeadline
 * gradeScale                — шкала оценок
 */
@Data
public class ScoringConfig {
    private int        testTimeoutSeconds        = 300;
    private double     styleDeductionPercent     = 10.0;
    private double     docsDeductionPercent      = 10.0;
    private double     softDeadlinePenaltyPerDay = 1.0;
    private GradeScale gradeScale                = new GradeScale();
}

package ru.nsu.krasnyanskii.checker;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import ru.nsu.krasnyanskii.model.OopCheckerConfig;
import ru.nsu.krasnyanskii.model.ScoringConfig;
import ru.nsu.krasnyanskii.model.Task;
import ru.nsu.krasnyanskii.model.results.BuildStatus;
import ru.nsu.krasnyanskii.model.results.TaskCheckResult;
import ru.nsu.krasnyanskii.model.results.TestCounts;

/**
 * Calculates the score for a student's task result.
 *
 * <p>Formula (applied in order):
 * <ol>
 *   <li>Compile failure &rarr; 0</li>
 *   <li>Base score from test-pass ratio &times; maxScore</li>
 *   <li>Style / docs deductions (configurable percentage)</li>
 *   <li>Soft-deadline penalty per day late; hard-deadline &rarr; 0</li>
 *   <li>Bonus added, capped at maxScore</li>
 * </ol>
 * Final score is rounded to one decimal place.
 */
public class ScoreCalculator {

    private final OopCheckerConfig config;

    /**
     * Creates a ScoreCalculator.
     *
     * @param config parsed OOP checker configuration
     */
    public ScoreCalculator(OopCheckerConfig config) {
        this.config = config;
    }

    /**
     * Calculates the final score for a task result.
     *
     * @param studentGithub student's GitHub login
     * @param result        checked task result
     * @return final score, rounded to one decimal place
     */
    public double calculate(String studentGithub, TaskCheckResult result) {
        Task task = config.findTaskById(result.getTaskId()).orElse(null);
        if (task == null) {
            return 0.0;
        }

        if (result.getCompileStatus() != BuildStatus.SUCCESS) {
            return 0.0;
        }

        ScoringConfig sc = config.getScoringConfig();

        double score = baseScore(result.getTestCounts(), task);
        score = applyDeductions(score, result, task, sc);
        score = applyDeadlinePenalty(score, result.getLastCommitDate(), task, sc);
        score = applyBonus(score, studentGithub, task);

        return Math.round(score * 10.0) / 10.0;
    }

    /**
     * Calculates the base score from the test-pass ratio.
     * If there are no tests, the full score is awarded (compiled = done).
     *
     * @param tests test counts from the task run
     * @param task  task configuration with maxScore
     * @return base score before any deductions
     */
    private double baseScore(TestCounts tests, Task task) {
        if (tests.getTotal() == 0) {
            return task.getMaxScore();
        }
        return ((double) tests.getPassed() / tests.getTotal()) * task.getMaxScore();
    }

    /**
     * Applies style and docs deductions, flooring the result at zero.
     *
     * @param score  score before deductions
     * @param result task check result with style/docs statuses
     * @param task   task configuration with maxScore
     * @param sc     scoring configuration with deduction percentages
     * @return score after deductions, at least 0
     */
    private double applyDeductions(double score, TaskCheckResult result,
                                   Task task, ScoringConfig sc) {
        double adjusted = score;
        if (result.getStyleStatus() == BuildStatus.FAILED) {
            adjusted -= task.getMaxScore() * sc.getStyleDeductionPercent() / 100.0;
        }
        if (result.getDocsStatus() == BuildStatus.FAILED) {
            adjusted -= task.getMaxScore() * sc.getDocsDeductionPercent() / 100.0;
        }
        return Math.max(0, adjusted);
    }

    /**
     * Applies deadline penalties.
     * Hard deadline exceeded &rarr; returns 0.
     * Soft deadline exceeded &rarr; subtracts {@code penaltyPerDay * daysLate}.
     *
     * @param score      score before penalties
     * @param lastCommit date of the student's last relevant commit
     * @param task       task configuration with deadlines
     * @param sc         scoring configuration with penalty rate
     * @return score after deadline penalties, at least 0
     */
    private double applyDeadlinePenalty(double score, LocalDate lastCommit,
                                        Task task, ScoringConfig sc) {
        if (lastCommit == null) {
            return score;
        }
        if (task.getHardDeadline() != null && lastCommit.isAfter(task.getHardDeadline())) {
            return 0.0;
        }
        if (task.getSoftDeadline() != null && lastCommit.isAfter(task.getSoftDeadline())) {
            long daysLate = ChronoUnit.DAYS.between(task.getSoftDeadline(), lastCommit);
            return Math.max(0, score - daysLate * sc.getSoftDeadlinePenaltyPerDay());
        }
        return score;
    }

    /**
     * Adds the configured bonus and caps the result at {@code task.getMaxScore()}.
     *
     * @param score         score before bonus
     * @param studentGithub student GitHub login
     * @param task          task configuration with maxScore
     * @return score after bonus, capped at maxScore
     */
    private double applyBonus(double score, String studentGithub, Task task) {
        double bonus = config.getBonusFor(studentGithub, task.getId());
        return Math.min(task.getMaxScore(), score + bonus);
    }
}

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
        if (isAfterHardDeadline(result.getLastCommitDate(), task)) {
            return 0.0;
        }

        ScoringConfig sc = config.getScoringConfig();

        double base      = baseScore(result.getTestCounts(), task);
        double deduction = deductionAmount(result, task, sc);
        double penalty   = softDeadlinePenalty(result.getLastCommitDate(), task, sc);
        double bonus     = bonusAmount(studentGithub, task);

        double score = Math.min(task.getMaxScore(), Math.max(0, base + deduction + penalty) + bonus);
        return Math.round(score * 10.0) / 10.0;
    }

    /**
     * Returns {@code true} if the commit is past the hard deadline.
     *
     * @param lastCommit date of the student's last relevant commit
     * @param task       task configuration with hard deadline
     * @return {@code true} if hard deadline exceeded
     */
    private boolean isAfterHardDeadline(LocalDate lastCommit, Task task) {
        return lastCommit != null
                && task.getHardDeadline() != null
                && lastCommit.isAfter(task.getHardDeadline());
    }

    /**
     * Returns the base score from the test-pass ratio.
     * If there are no tests, the full score is awarded (compiled = done).
     *
     * @param tests test counts from the task run
     * @param task  task configuration with maxScore
     * @return base score, always &ge; 0
     */
    private double baseScore(TestCounts tests, Task task) {
        if (tests.getTotal() == 0) {
            return task.getMaxScore();
        }
        return ((double) tests.getPassed() / tests.getTotal()) * task.getMaxScore();
    }

    /**
     * Returns the total style and docs deduction as a non-positive number.
     *
     * @param result task check result with style/docs statuses
     * @param task   task configuration with maxScore
     * @param sc     scoring configuration with deduction percentages
     * @return deduction amount (&le; 0)
     */
    private double deductionAmount(TaskCheckResult result, Task task, ScoringConfig sc) {
        double deduction = 0;
        if (result.getStyleStatus() == BuildStatus.FAILED) {
            deduction -= task.getMaxScore() * sc.getStyleDeductionPercent() / 100.0;
        }
        if (result.getDocsStatus() == BuildStatus.FAILED) {
            deduction -= task.getMaxScore() * sc.getDocsDeductionPercent() / 100.0;
        }
        return deduction;
    }

    /**
     * Returns the soft-deadline penalty as a non-positive number.
     * Returns 0 if no soft deadline is configured or not exceeded.
     *
     * @param lastCommit date of the student's last relevant commit
     * @param task       task configuration with soft deadline
     * @param sc         scoring configuration with penalty rate per day
     * @return penalty amount (&le; 0)
     */
    private double softDeadlinePenalty(LocalDate lastCommit, Task task, ScoringConfig sc) {
        if (lastCommit == null || task.getSoftDeadline() == null) {
            return 0;
        }
        if (!lastCommit.isAfter(task.getSoftDeadline())) {
            return 0;
        }
        long daysLate = ChronoUnit.DAYS.between(task.getSoftDeadline(), lastCommit);
        return -(daysLate * sc.getSoftDeadlinePenaltyPerDay());
    }

    /**
     * Returns the bonus points for this student and task.
     *
     * @param studentGithub student GitHub login
     * @param task          task configuration
     * @return bonus amount (&ge; 0)
     */
    private double bonusAmount(String studentGithub, Task task) {
        return config.getBonusFor(studentGithub, task.getId());
    }
}

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
 * Formula: test-pass ratio * maxScore, minus style/docs deductions,
 * minus soft-deadline penalty per day, capped to 0 after hard deadline,
 * then bonus added (capped at maxScore).
 */
public class ScoreCalculator {
    private final OopCheckerConfig config;

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

        ScoringConfig sc = config.getScoringConfig();

        // Compile is the gate; no compile = 0
        if (result.getCompileStatus() != BuildStatus.SUCCESS) {
            return 0.0;
        }

        // Base score from tests
        TestCounts tests = result.getTestCounts();
        double baseScore;
        if (tests.getTotal() == 0) {
            baseScore = task.getMaxScore(); // compiled but no tests: full score
        } else {
            baseScore = ((double) tests.getPassed() / tests.getTotal()) * task.getMaxScore();
        }

        // Style and docs deductions
        if (result.getStyleStatus() == BuildStatus.FAILED) {
            baseScore -= task.getMaxScore() * sc.getStyleDeductionPercent() / 100.0;
        }
        if (result.getDocsStatus() == BuildStatus.FAILED) {
            baseScore -= task.getMaxScore() * sc.getDocsDeductionPercent() / 100.0;
        }
        baseScore = Math.max(0, baseScore);

        // Deadline penalties
        LocalDate lastCommit = result.getLastCommitDate();
        if (lastCommit != null) {
            if (task.getHardDeadline() != null && lastCommit.isAfter(task.getHardDeadline())) {
                return 0.0;
            }
            if (task.getSoftDeadline() != null && lastCommit.isAfter(task.getSoftDeadline())) {
                long daysLate = ChronoUnit.DAYS.between(task.getSoftDeadline(), lastCommit);
                baseScore -= daysLate * sc.getSoftDeadlinePenaltyPerDay();
                baseScore = Math.max(0, baseScore);
            }
        }

        // Bonus (capped at maxScore)
        double bonus = config.getBonusFor(studentGithub, task.getId());
        baseScore = Math.min(task.getMaxScore(), baseScore + bonus);

        return Math.round(baseScore * 10.0) / 10.0;
    }
}

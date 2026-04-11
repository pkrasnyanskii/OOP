package org.example.checker;

import org.example.model.OopCheckerConfig;
import org.example.model.ScoringConfig;
import org.example.model.Task;
import org.example.model.results.BuildStatus;
import org.example.model.results.TaskCheckResult;
import org.example.model.results.TestCounts;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class ScoreCalculator {
    private final OopCheckerConfig config;

    public ScoreCalculator(OopCheckerConfig config) {
        this.config = config;
    }

    public double calculate(String studentGithub, TaskCheckResult result) {
        Task task = config.findTaskById(result.getTaskId()).orElse(null);
        if (task == null) return 0.0;

        ScoringConfig sc = config.getScoringConfig();

        // Compilation is the gate
        if (result.getCompileStatus() != BuildStatus.SUCCESS) {
            return 0.0;
        }

        // Test-based base score
        TestCounts tests = result.getTestCounts();
        double baseScore;
        if (tests.getTotal() == 0) {
            // No tests: give full score if compiled
            baseScore = task.getMaxScore();
        } else {
            baseScore = ((double) tests.getPassed() / tests.getTotal()) * task.getMaxScore();
        }

        // Deductions
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

        // Bonus points (capped at maxScore)
        double bonus = config.getBonusFor(studentGithub, task.getId());
        baseScore = Math.min(task.getMaxScore(), baseScore + bonus);

        return Math.round(baseScore * 10.0) / 10.0;
    }
}

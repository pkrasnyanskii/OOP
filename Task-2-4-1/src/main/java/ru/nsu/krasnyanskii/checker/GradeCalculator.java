package ru.nsu.krasnyanskii.checker;

import java.util.List;
import ru.nsu.krasnyanskii.model.CheckPoint;
import ru.nsu.krasnyanskii.model.OopCheckerConfig;
import ru.nsu.krasnyanskii.model.Task;
import ru.nsu.krasnyanskii.model.results.StudentCheckResult;
import ru.nsu.krasnyanskii.model.results.TaskCheckResult;

/**
 * Computes grades, percentages, and aggregated scores for the HTML report.
 *
 * <p>Extracted from {@link ru.nsu.krasnyanskii.report.HtmlReporter} so that
 * the reporter focuses solely on rendering HTML (SRP).</p>
 */
public class GradeCalculator {

    private final OopCheckerConfig config;

    /**
     * Creates a GradeCalculator.
     *
     * @param config parsed OOP checker configuration
     */
    public GradeCalculator(OopCheckerConfig config) {
        this.config = config;
    }

    /**
     * Converts a raw score to a percentage of the maximum.
     *
     * @param score    earned score
     * @param maxScore maximum possible score
     * @return percentage in [0, 100], or 0 if maxScore is zero
     */
    public double scorePercent(double score, double maxScore) {
        return maxScore > 0 ? score / maxScore * 100 : 0;
    }

    /**
     * Converts a raw score to an academic grade (2–5).
     *
     * @param score    earned score
     * @param maxScore maximum possible score
     * @return grade from 2 (fail) to 5 (excellent)
     */
    public int grade(double score, double maxScore) {
        double pct = scorePercent(score, maxScore > 0 ? maxScore : 100);
        return config.getScoringConfig().getGradeScale().toGrade(pct);
    }

    /**
     * Maps a numeric grade to a CSS class name.
     *
     * @param gradeValue academic grade (2–5)
     * @return CSS class: {@code pass}, {@code warn}, or {@code fail}
     */
    public String gradeClass(int gradeValue) {
        return switch (gradeValue) {
            case 5, 4 -> "pass";
            case 3    -> "warn";
            default   -> "fail";
        };
    }

    /**
     * Sums the maximum scores for the given task IDs,
     * including the activity bonus if configured.
     *
     * @param taskIds list of task identifiers to include
     * @return total maximum score achievable
     */
    public double totalMaxScore(List<String> taskIds) {
        double total = taskIds.stream()
                .mapToDouble(tid -> config.findTaskById(tid)
                        .map(Task::getMaxScore).orElse(0.0))
                .sum();
        if (config.getActivityConfig() != null) {
            total += config.getActivityConfig().getBonusPoints();
        }
        return total;
    }

    /**
     * Sums the earned scores for all tasks in a checkpoint.
     *
     * @param sr student result to read scores from
     * @param cp checkpoint whose task IDs define the scope
     * @return total score earned across checkpoint tasks
     */
    public double checkpointScore(StudentCheckResult sr, CheckPoint cp) {
        return cp.getTaskIds().stream()
                .mapToDouble(tid -> sr.getTaskResult(tid)
                        .map(TaskCheckResult::getScore).orElse(0.0))
                .sum();
    }

    /**
     * Sums the maximum scores for all tasks in a checkpoint.
     *
     * @param cp checkpoint whose task IDs define the scope
     * @return total maximum score for checkpoint tasks
     */
    public double checkpointMax(CheckPoint cp) {
        return cp.getTaskIds().stream()
                .mapToDouble(tid -> config.findTaskById(tid)
                        .map(Task::getMaxScore).orElse(0.0))
                .sum();
    }
}

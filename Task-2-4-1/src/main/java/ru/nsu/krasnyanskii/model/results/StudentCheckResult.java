package ru.nsu.krasnyanskii.model.results;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Aggregated check result for one student: task results plus activity bonus. */
@Getter
@Setter
public class StudentCheckResult {
    private final String studentGithub;
    private final String studentName;
    private final String groupName;

    private final List<TaskCheckResult> taskResults = new ArrayList<>();

    private int    activeWeeks   = 0;
    private double activityBonus = 0.0;

    public StudentCheckResult(String studentGithub, String studentName, String groupName) {
        this.studentGithub = studentGithub;
        this.studentName   = studentName;
        this.groupName     = groupName;
    }

    /** Appends a task result. */
    public void addTaskResult(TaskCheckResult result) {
        taskResults.add(result);
    }

    /**
     * Finds the result for a specific task.
     *
     * @param taskId task identifier
     * @return Optional containing the result, or empty
     */
    public Optional<TaskCheckResult> getTaskResult(String taskId) {
        return taskResults.stream()
                .filter(r -> r.getTaskId().equals(taskId))
                .findFirst();
    }

    /**
     * Returns total score across all tasks plus the activity bonus.
     *
     * @return total score
     */
    public double getTotalScore() {
        return taskResults.stream().mapToDouble(TaskCheckResult::getScore).sum() + activityBonus;
    }
}

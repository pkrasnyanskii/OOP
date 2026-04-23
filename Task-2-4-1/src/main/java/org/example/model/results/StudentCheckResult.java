package org.example.model.results;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Совокупный результат проверки одного студента:
 * список результатов по задачам + активность за семестр.
 */
@Getter
@Setter
public class StudentCheckResult {
    private final String studentGithub;
    private final String studentName;
    private final String groupName;

    private final List<TaskCheckResult> taskResults = new ArrayList<>();

    /** Количество «активных» недель (≥1 коммит) за период курса. */
    private int    activeWeeks    = 0;
    /** Бонус за активность (вычисляется ActivityTracker). */
    private double activityBonus  = 0.0;

    public StudentCheckResult(String studentGithub, String studentName, String groupName) {
        this.studentGithub = studentGithub;
        this.studentName   = studentName;
        this.groupName     = groupName;
    }

    public void addTaskResult(TaskCheckResult result) {
        taskResults.add(result);
    }

    public Optional<TaskCheckResult> getTaskResult(String taskId) {
        return taskResults.stream()
                .filter(r -> r.getTaskId().equals(taskId))
                .findFirst();
    }

    /** Сумма баллов за все задачи плюс бонус за активность. */
    public double getTotalScore() {
        return taskResults.stream().mapToDouble(TaskCheckResult::getScore).sum() + activityBonus;
    }
}

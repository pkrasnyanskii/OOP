package org.example.model.results;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StudentCheckResult {
    private final String studentGithub;
    private final String studentName;
    private final String groupName;
    private final List<TaskCheckResult> taskResults = new ArrayList<>();
    private int activeWeeks = 0;
    private double activityBonus = 0.0;

    public StudentCheckResult(String studentGithub, String studentName, String groupName) {
        this.studentGithub = studentGithub;
        this.studentName = studentName;
        this.groupName = groupName;
    }

    public String getStudentGithub() { return studentGithub; }
    public String getStudentName() { return studentName; }
    public String getGroupName() { return groupName; }

    public List<TaskCheckResult> getTaskResults() { return taskResults; }

    public void addTaskResult(TaskCheckResult result) {
        taskResults.add(result);
    }

    public Optional<TaskCheckResult> getTaskResult(String taskId) {
        return taskResults.stream().filter(r -> r.getTaskId().equals(taskId)).findFirst();
    }

    public int getActiveWeeks() { return activeWeeks; }
    public void setActiveWeeks(int activeWeeks) { this.activeWeeks = activeWeeks; }

    public double getActivityBonus() { return activityBonus; }
    public void setActivityBonus(double activityBonus) { this.activityBonus = activityBonus; }

    public double getTotalScore() {
        double sum = taskResults.stream().mapToDouble(TaskCheckResult::getScore).sum();
        return sum + activityBonus;
    }

    public double getTotalMaxScore() {
        return taskResults.stream()
                .mapToDouble(r -> r.getScore() >= 0 ? r.getScore() : 0)
                .count();
    }
}

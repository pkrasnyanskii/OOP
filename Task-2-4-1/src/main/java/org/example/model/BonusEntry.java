package org.example.model;

public class BonusEntry {
    private final String studentGithub;
    private final String taskId;
    private final double points;

    public BonusEntry(String studentGithub, String taskId, double points) {
        this.studentGithub = studentGithub;
        this.taskId = taskId;
        this.points = points;
    }

    public String getStudentGithub() { return studentGithub; }
    public String getTaskId() { return taskId; }
    public double getPoints() { return points; }
}

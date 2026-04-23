package org.example.model;

import java.time.LocalDate;

public class Task {
    private String id;
    private String name;
    private double maxScore;
    private LocalDate softDeadline;
    private LocalDate hardDeadline;

    public Task() {}

    public Task(String id, String name, double maxScore, LocalDate softDeadline, LocalDate hardDeadline) {
        this.id = id;
        this.name = name;
        this.maxScore = maxScore;
        this.softDeadline = softDeadline;
        this.hardDeadline = hardDeadline;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getMaxScore() { return maxScore; }
    public void setMaxScore(double maxScore) { this.maxScore = maxScore; }

    public LocalDate getSoftDeadline() { return softDeadline; }
    public void setSoftDeadline(LocalDate softDeadline) { this.softDeadline = softDeadline; }

    public LocalDate getHardDeadline() { return hardDeadline; }
    public void setHardDeadline(LocalDate hardDeadline) { this.hardDeadline = hardDeadline; }

    @Override
    public String toString() {
        return "Task{id='" + id + "', name='" + name + "', maxScore=" + maxScore + "}";
    }
}

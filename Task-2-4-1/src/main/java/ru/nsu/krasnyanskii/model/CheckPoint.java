package ru.nsu.krasnyanskii.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** A grading checkpoint in the course (e.g. midterm or final). */
@Getter
@Setter
@NoArgsConstructor
public class CheckPoint {
    private String       name;
    private LocalDate    date;
    private List<String> taskIds = new ArrayList<>();
    private boolean      isFinal = false;

    public CheckPoint(String name, LocalDate date) {
        this.name = name;
        this.date = date;
    }

    /** Adds a task id that counts toward this checkpoint. */
    public void addTaskId(String taskId) {
        this.taskIds.add(taskId);
    }
}

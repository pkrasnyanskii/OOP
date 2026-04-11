package org.example.dsl.builders

import org.example.model.Task

import java.time.LocalDate

class TaskBuilder {
    String id
    String name = ""
    double maxScore = 100.0
    String softDeadline
    String hardDeadline

    TaskBuilder(String id) {
        this.id = id
    }

    Task build() {
        return new Task(
            id,
            name,
            maxScore,
            softDeadline ? LocalDate.parse(softDeadline) : null,
            hardDeadline ? LocalDate.parse(hardDeadline) : null
        )
    }
}

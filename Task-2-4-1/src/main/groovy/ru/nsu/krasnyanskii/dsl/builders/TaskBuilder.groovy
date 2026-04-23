package ru.nsu.krasnyanskii.dsl.builders

import ru.nsu.krasnyanskii.model.Task

import java.time.LocalDate

/** Builds a Task model object from DSL property assignments. */
class TaskBuilder {
    String id
    String name         = ""
    double maxScore     = 100.0
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

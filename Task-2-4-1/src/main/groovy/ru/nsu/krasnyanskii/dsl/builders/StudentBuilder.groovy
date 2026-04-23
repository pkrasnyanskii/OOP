package ru.nsu.krasnyanskii.dsl.builders

import ru.nsu.krasnyanskii.model.Student

/** Builds a Student model object from DSL property assignments. */
class StudentBuilder {
    String github = ""
    String name   = ""
    String repo   = ""

    Student build() {
        return new Student(github, name, repo)
    }
}

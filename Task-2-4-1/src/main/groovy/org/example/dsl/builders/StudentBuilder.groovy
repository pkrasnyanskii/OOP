package org.example.dsl.builders

import org.example.model.Student

class StudentBuilder {
    String github = ""
    String name = ""
    String repo = ""

    Student build() {
        return new Student(github, name, repo)
    }
}

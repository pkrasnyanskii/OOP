package org.example.dsl.builders

import org.example.model.Group

class GroupBuilder {
    private final String name
    private final Group group

    GroupBuilder(String name) {
        this.name = name
        this.group = new Group(name)
    }

    void student(Closure closure) {
        def builder = new StudentBuilder()
        closure.delegate = builder
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.call()
        group.addStudent(builder.build())
    }

    Group build() { return group }
}

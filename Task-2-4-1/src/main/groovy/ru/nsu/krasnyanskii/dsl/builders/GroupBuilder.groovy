package ru.nsu.krasnyanskii.dsl.builders

import ru.nsu.krasnyanskii.model.Group

/** Handles the {@code group("name") { student { ... } }} DSL block. */
class GroupBuilder {
    private final String name
    private final Group  group

    GroupBuilder(String name) {
        this.name  = name
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

package ru.nsu.krasnyanskii.dsl.builders

import ru.nsu.krasnyanskii.model.OopCheckerConfig

/** Handles the {@code tasks { task("id") { ... } }} DSL block. */
class TasksBuilder {
    private final OopCheckerConfig config

    TasksBuilder(OopCheckerConfig config) {
        this.config = config
    }

    void task(String id, Closure closure) {
        def builder = new TaskBuilder(id)
        closure.delegate = builder
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.call()
        config.addTask(builder.build())
    }
}

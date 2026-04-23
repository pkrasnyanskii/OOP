package org.example.dsl.builders

import org.example.model.OopCheckerConfig

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

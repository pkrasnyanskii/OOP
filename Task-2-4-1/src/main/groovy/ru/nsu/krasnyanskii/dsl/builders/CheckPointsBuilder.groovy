package ru.nsu.krasnyanskii.dsl.builders

import ru.nsu.krasnyanskii.model.OopCheckerConfig

/** Handles the {@code checkPoints { checkPoint("name") { ... } }} DSL block. */
class CheckPointsBuilder {
    private final OopCheckerConfig config

    CheckPointsBuilder(OopCheckerConfig config) {
        this.config = config
    }

    void checkPoint(String name, Closure closure) {
        def builder = new CheckPointBuilder(name)
        closure.delegate = builder
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.call()
        config.addCheckPoint(builder.build())
    }
}

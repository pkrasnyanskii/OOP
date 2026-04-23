package org.example.dsl.builders

import org.example.model.OopCheckerConfig

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

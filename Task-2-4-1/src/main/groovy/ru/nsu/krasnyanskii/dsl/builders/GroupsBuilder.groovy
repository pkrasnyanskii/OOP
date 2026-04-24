package ru.nsu.krasnyanskii.dsl.builders

import ru.nsu.krasnyanskii.model.OopCheckerConfig

/** Handles the {@code groups { group("name") { ... } }} DSL block. */
class GroupsBuilder {
    private final OopCheckerConfig config

    GroupsBuilder(OopCheckerConfig config) {
        this.config = config
    }

    void group(String name, Closure closure) {
        def builder = new GroupBuilder(name)
        closure.delegate = builder
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.call()
        config.addGroup(builder.build())
    }
}

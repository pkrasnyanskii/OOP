package ru.nsu.krasnyanskii.dsl.builders

import ru.nsu.krasnyanskii.model.OopCheckerConfig

/**
 * Handles the {@code bonus { student "login" task "Task-X" points 5 }} DSL block.
 * Each call to {@code student} starts a fluent chain: student → task → points.
 */
class BonusBuilder {
    private final OopCheckerConfig config

    BonusBuilder(OopCheckerConfig config) {
        this.config = config
    }

    StudentBonusBuilder student(String github) {
        return new StudentBonusBuilder(config, github)
    }
}

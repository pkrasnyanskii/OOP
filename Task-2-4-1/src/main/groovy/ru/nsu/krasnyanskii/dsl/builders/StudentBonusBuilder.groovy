package ru.nsu.krasnyanskii.dsl.builders

import ru.nsu.krasnyanskii.model.OopCheckerConfig

/** Second step in the bonus fluent chain: {@code student(...).task("Task-X")}. */
class StudentBonusBuilder {
    private final OopCheckerConfig config
    private final String           github

    StudentBonusBuilder(OopCheckerConfig config, String github) {
        this.config = config
        this.github = github
    }

    TaskBonusBuilder task(String taskId) {
        return new TaskBonusBuilder(config, github, taskId)
    }
}

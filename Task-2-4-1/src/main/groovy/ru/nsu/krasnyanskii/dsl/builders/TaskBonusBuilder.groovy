package ru.nsu.krasnyanskii.dsl.builders

import ru.nsu.krasnyanskii.model.BonusEntry
import ru.nsu.krasnyanskii.model.OopCheckerConfig

/** Final step in the bonus fluent chain: {@code student(...).task(...).points(5)}. */
class TaskBonusBuilder {
    private final OopCheckerConfig config
    private final String           github
    private final String           taskId

    TaskBonusBuilder(OopCheckerConfig config, String github, String taskId) {
        this.config = config
        this.github = github
        this.taskId = taskId
    }

    void points(double pts) {
        config.addBonusEntry(new BonusEntry(github, taskId, pts))
    }
}

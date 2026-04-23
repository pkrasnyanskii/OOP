package org.example.dsl.builders

import org.example.model.BonusEntry
import org.example.model.OopCheckerConfig

class TaskBonusBuilder {
    private final OopCheckerConfig config
    private final String github
    private final String taskId

    TaskBonusBuilder(OopCheckerConfig config, String github, String taskId) {
        this.config = config
        this.github = github
        this.taskId = taskId
    }

    void points(double pts) {
        config.addBonusEntry(new BonusEntry(github, taskId, pts))
    }
}

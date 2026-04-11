package org.example.dsl.builders

import org.example.model.OopCheckerConfig

class StudentBonusBuilder {
    private final OopCheckerConfig config
    private final String github

    StudentBonusBuilder(OopCheckerConfig config, String github) {
        this.config = config
        this.github = github
    }

    TaskBonusBuilder task(String taskId) {
        return new TaskBonusBuilder(config, github, taskId)
    }
}

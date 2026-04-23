package org.example.dsl.builders

import org.example.model.OopCheckerConfig

/**
 * DSL для задания дополнительных баллов:
 *
 *   bonus {
 *       student "ivanov-ivan" task "Task-1-1" points 5
 *       student "petrov-petr" task "Task-1-2" points 3
 *   }
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

package org.example.dsl.builders

import org.example.model.ActivityConfig

import java.time.LocalDate

/**
 * DSL для настройки отслеживания активности студентов (доп. задание):
 *
 *   activity {
 *       courseStart    = "2024-02-05"
 *       courseEnd      = "2024-06-20"
 *       minActiveWeeks = 10
 *       bonusPoints    = 15
 *   }
 */
class ActivityBuilder {
    String courseStart
    String courseEnd
    int minActiveWeeks = 10
    double bonusPoints = 15.0

    ActivityConfig build() {
        def cfg = new ActivityConfig()
        if (courseStart) cfg.setCourseStart(LocalDate.parse(courseStart))
        if (courseEnd)   cfg.setCourseEnd(LocalDate.parse(courseEnd))
        cfg.setMinActiveWeeks(minActiveWeeks)
        cfg.setBonusPoints(bonusPoints)
        return cfg
    }
}

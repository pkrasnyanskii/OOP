package ru.nsu.krasnyanskii.dsl.builders

import ru.nsu.krasnyanskii.model.ActivityConfig

import java.time.LocalDate

/**
 * Handles the {@code activity { courseStart=...; courseEnd=...; minActiveWeeks=...; bonusPoints=... }} DSL block.
 */
class ActivityBuilder {
    String courseStart
    String courseEnd
    int    minActiveWeeks = 10
    double bonusPoints    = 15.0

    ActivityConfig build() {
        def cfg = new ActivityConfig()
        if (courseStart) cfg.setCourseStart(LocalDate.parse(courseStart))
        if (courseEnd)   cfg.setCourseEnd(LocalDate.parse(courseEnd))
        cfg.setMinActiveWeeks(minActiveWeeks)
        cfg.setBonusPoints(bonusPoints)
        return cfg
    }
}

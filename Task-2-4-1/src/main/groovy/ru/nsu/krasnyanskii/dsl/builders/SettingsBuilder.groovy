package ru.nsu.krasnyanskii.dsl.builders

import ru.nsu.krasnyanskii.model.OopCheckerConfig
import ru.nsu.krasnyanskii.model.ScoringConfig

/** Handles the {@code settings { ... }} DSL block. */
class SettingsBuilder {
    private final OopCheckerConfig config
    private final ScoringConfig    sc

    SettingsBuilder(OopCheckerConfig config) {
        this.config = config
        this.sc     = config.getScoringConfig()
    }

    void setTestTimeout(int seconds)               { sc.setTestTimeoutSeconds(seconds) }
    void setStyleDeduction(double percent)         { sc.setStyleDeductionPercent(percent) }
    void setDocsDeduction(double percent)          { sc.setDocsDeductionPercent(percent) }
    void setSoftDeadlinePenaltyPerDay(double pts)  { sc.setSoftDeadlinePenaltyPerDay(pts) }

    /** Configures the grade scale: {@code gradeScale { excellent=90; good=75; satisfactory=60 }} */
    void gradeScale(Closure closure) {
        def builder = new GradeScaleBuilder(sc.getGradeScale())
        closure.delegate = builder
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.call()
    }

    /** Configures bonus points: {@code bonus { student "login" task "Task-X" points 5 }} */
    void bonus(Closure closure) {
        def builder = new BonusBuilder(config)
        closure.delegate = builder
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.call()
    }

    /** Configures weekly activity tracking (bonus task). */
    void activity(Closure closure) {
        def builder = new ActivityBuilder()
        closure.delegate = builder
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.call()
        config.setActivityConfig(builder.build())
    }
}

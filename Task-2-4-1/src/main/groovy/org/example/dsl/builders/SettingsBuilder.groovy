package org.example.dsl.builders

import org.example.model.OopCheckerConfig
import org.example.model.ScoringConfig

class SettingsBuilder {
    private final OopCheckerConfig config
    private final ScoringConfig sc

    SettingsBuilder(OopCheckerConfig config) {
        this.config = config
        this.sc = config.getScoringConfig()
    }

    /** Таймаут выполнения тестов в секундах */
    void setTestTimeout(int seconds) {
        sc.setTestTimeoutSeconds(seconds)
    }

    /** Штраф за нарушение стиля в % от maxScore задачи */
    void setStyleDeduction(double percent) {
        sc.setStyleDeductionPercent(percent)
    }

    /** Штраф за отсутствие документации в % от maxScore */
    void setDocsDeduction(double percent) {
        sc.setDocsDeductionPercent(percent)
    }

    /** Штраф за каждый день просрочки после softDeadline */
    void setSoftDeadlinePenaltyPerDay(double points) {
        sc.setSoftDeadlinePenaltyPerDay(points)
    }

    /** Шкала оценок: gradeScale { excellent=90; good=75; satisfactory=60 } */
    void gradeScale(Closure closure) {
        def builder = new GradeScaleBuilder(sc.getGradeScale())
        closure.delegate = builder
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.call()
    }

    /** Дополнительные баллы: bonus { student "github" task "Task-X" points 5 } */
    void bonus(Closure closure) {
        def builder = new BonusBuilder(config)
        closure.delegate = builder
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.call()
    }

    /** Настройка отслеживания активности студентов (доп. задание) */
    void activity(Closure closure) {
        def builder = new ActivityBuilder()
        closure.delegate = builder
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.call()
        config.setActivityConfig(builder.build())
    }
}

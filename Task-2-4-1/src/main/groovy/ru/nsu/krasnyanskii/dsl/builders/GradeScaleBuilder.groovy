package ru.nsu.krasnyanskii.dsl.builders

import ru.nsu.krasnyanskii.model.GradeScale

/** Delegates grade scale threshold assignments to the GradeScale model. */
class GradeScaleBuilder {
    private final GradeScale scale

    GradeScaleBuilder(GradeScale scale) {
        this.scale = scale
    }

    void setExcellent(double threshold)    { scale.setExcellent(threshold) }
    void setGood(double threshold)         { scale.setGood(threshold) }
    void setSatisfactory(double threshold) { scale.setSatisfactory(threshold) }
}

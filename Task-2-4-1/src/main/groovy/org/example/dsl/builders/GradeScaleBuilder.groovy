package org.example.dsl.builders

import org.example.model.GradeScale

class GradeScaleBuilder {
    private final GradeScale scale

    GradeScaleBuilder(GradeScale scale) {
        this.scale = scale
    }

    void setExcellent(double threshold) { scale.setExcellent(threshold) }
    void setGood(double threshold)      { scale.setGood(threshold) }
    void setSatisfactory(double threshold) { scale.setSatisfactory(threshold) }
}

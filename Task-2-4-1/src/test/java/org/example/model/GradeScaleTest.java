package org.example.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("GradeScale — перевод процента в оценку")
class GradeScaleTest {

    private GradeScale scale;

    @BeforeEach
    void setUp() {
        scale = new GradeScale(); // excellent=90, good=75, satisfactory=60
    }

    @Test
    @DisplayName("100% → оценка 5")
    void fullScoreIsExcellent() {
        assertEquals(5, scale.toGrade(100.0));
    }

    @Test
    @DisplayName("Ровно 90% → оценка 5")
    void exactExcellentThreshold() {
        assertEquals(5, scale.toGrade(90.0));
    }

    @Test
    @DisplayName("89.9% → оценка 4")
    void justBelowExcellent() {
        assertEquals(4, scale.toGrade(89.9));
    }

    @Test
    @DisplayName("Ровно 75% → оценка 4")
    void exactGoodThreshold() {
        assertEquals(4, scale.toGrade(75.0));
    }

    @Test
    @DisplayName("74.9% → оценка 3")
    void justBelowGood() {
        assertEquals(3, scale.toGrade(74.9));
    }

    @Test
    @DisplayName("Ровно 60% → оценка 3")
    void exactSatisfactoryThreshold() {
        assertEquals(3, scale.toGrade(60.0));
    }

    @Test
    @DisplayName("59.9% → оценка 2")
    void justBelowSatisfactory() {
        assertEquals(2, scale.toGrade(59.9));
    }

    @Test
    @DisplayName("0% → оценка 2")
    void zeroIsUnsatisfactory() {
        assertEquals(2, scale.toGrade(0.0));
    }

    @Test
    @DisplayName("Кастомная шкала применяется корректно")
    void customThresholdsApplied() {
        scale.setExcellent(95);
        scale.setGood(80);
        scale.setSatisfactory(65);

        assertEquals(5, scale.toGrade(95.0));
        assertEquals(4, scale.toGrade(80.0));
        assertEquals(3, scale.toGrade(65.0));
        assertEquals(2, scale.toGrade(64.9));
    }
}

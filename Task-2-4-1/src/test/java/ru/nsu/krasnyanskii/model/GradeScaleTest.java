package ru.nsu.krasnyanskii.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("GradeScale — percentage to grade mapping")
class GradeScaleTest {

    private GradeScale scale;

    @BeforeEach
    void setUp() {
        scale = new GradeScale(); // defaults: excellent=90, good=75, satisfactory=60
    }

    @Test
    @DisplayName("100% → grade 5")
    void fullScoreIsExcellent() {
        assertEquals(5, scale.toGrade(100.0));
    }

    @Test
    @DisplayName("Exactly 90% → grade 5")
    void exactExcellentThreshold() {
        assertEquals(5, scale.toGrade(90.0));
    }

    @Test
    @DisplayName("89.9% → grade 4")
    void justBelowExcellent() {
        assertEquals(4, scale.toGrade(89.9));
    }

    @Test
    @DisplayName("Exactly 75% → grade 4")
    void exactGoodThreshold() {
        assertEquals(4, scale.toGrade(75.0));
    }

    @Test
    @DisplayName("74.9% → grade 3")
    void justBelowGood() {
        assertEquals(3, scale.toGrade(74.9));
    }

    @Test
    @DisplayName("Exactly 60% → grade 3")
    void exactSatisfactoryThreshold() {
        assertEquals(3, scale.toGrade(60.0));
    }

    @Test
    @DisplayName("59.9% → grade 2")
    void justBelowSatisfactory() {
        assertEquals(2, scale.toGrade(59.9));
    }

    @Test
    @DisplayName("0% → grade 2")
    void zeroIsUnsatisfactory() {
        assertEquals(2, scale.toGrade(0.0));
    }

    @Test
    @DisplayName("Custom thresholds are applied correctly")
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

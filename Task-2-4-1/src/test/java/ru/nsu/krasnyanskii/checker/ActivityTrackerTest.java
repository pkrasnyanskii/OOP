package ru.nsu.krasnyanskii.checker;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ru.nsu.krasnyanskii.model.ActivityConfig;

@DisplayName("ActivityTracker — activity bonus calculation")
class ActivityTrackerTest {

    private ActivityTracker tracker;
    private ActivityConfig  cfg;

    @BeforeEach
    void setUp() {
        tracker = new ActivityTracker(null);

        cfg = new ActivityConfig();
        cfg.setCourseStart(LocalDate.of(2024, 2, 1));
        cfg.setCourseEnd(LocalDate.of(2024, 6, 30));
        cfg.setMinActiveWeeks(10);
        cfg.setBonusPoints(15.0);
    }

    @Test
    @DisplayName("activeWeeks >= minActiveWeeks → full bonus")
    void fullBonusWhenThresholdReached() {
        assertEquals(15.0, tracker.calculateActivityBonus(10, cfg), 0.001);
        assertEquals(15.0, tracker.calculateActivityBonus(20, cfg), 0.001);
    }

    @Test
    @DisplayName("0 active weeks → 0 bonus")
    void zeroBonusForZeroWeeks() {
        assertEquals(0.0, tracker.calculateActivityBonus(0, cfg), 0.001);
    }

    @Test
    @DisplayName("Partial bonus is proportional to activity")
    void partialBonusIsProportional() {
        assertEquals(7.0, tracker.calculateActivityBonus(5, cfg), 0.001);
    }

    @Test
    @DisplayName("null config → 0 bonus")
    void nullConfigReturnsZero() {
        assertEquals(0.0, tracker.calculateActivityBonus(100, null), 0.001);
    }

    @Test
    @DisplayName("1 active week of 10 → floor(1.5) = 1")
    void oneWeekOutOfTen() {
        assertEquals(1.0, tracker.calculateActivityBonus(1, cfg), 0.001);
    }

    @Test
    @DisplayName("countActiveWeeks with null config → 0")
    void countActiveWeeks_nullConfig(@TempDir Path tmpDir) {
        assertEquals(0, tracker.countActiveWeeks(tmpDir, null));
    }

    @Test
    @DisplayName("countActiveWeeks with null courseStart → 0")
    void countActiveWeeks_nullStart(@TempDir Path tmpDir) {
        ActivityConfig badCfg = new ActivityConfig();
        badCfg.setCourseEnd(LocalDate.of(2024, 6, 30));
        assertEquals(0, tracker.countActiveWeeks(tmpDir, badCfg));
    }

    @Test
    @DisplayName("countActiveWeeks with null courseEnd → 0")
    void countActiveWeeks_nullEnd(@TempDir Path tmpDir) {
        ActivityConfig badCfg = new ActivityConfig();
        badCfg.setCourseStart(LocalDate.of(2024, 2, 1));
        assertEquals(0, tracker.countActiveWeeks(tmpDir, badCfg));
    }
}

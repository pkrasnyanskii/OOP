package org.example.checker;

import org.example.model.ActivityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты ActivityTracker — подсчёт активных недель и бонусов.
 * GitManager не тестируется здесь (нет реальных репозиториев),
 * тестируем только логику начисления бонуса.
 */
@DisplayName("ActivityTracker — бонус за активность")
class ActivityTrackerTest {

    private ActivityTracker tracker;
    private ActivityConfig  cfg;

    @BeforeEach
    void setUp() {
        // GitManager передаём null — он не используется в тестируемых методах
        tracker = new ActivityTracker(null);

        cfg = new ActivityConfig();
        cfg.setCourseStart(LocalDate.of(2024, 2, 1));
        cfg.setCourseEnd(LocalDate.of(2024, 6, 30));
        cfg.setMinActiveWeeks(10);
        cfg.setBonusPoints(15.0);
    }

    @Test
    @DisplayName("activeWeeks >= minActiveWeeks → полный бонус")
    void fullBonusWhenThresholdReached() {
        assertEquals(15.0, tracker.calculateActivityBonus(10, cfg), 0.001);
        assertEquals(15.0, tracker.calculateActivityBonus(20, cfg), 0.001);
    }

    @Test
    @DisplayName("0 активных недель → бонус 0")
    void zeroBonusForZeroWeeks() {
        assertEquals(0.0, tracker.calculateActivityBonus(0, cfg), 0.001);
    }

    @Test
    @DisplayName("Частичный бонус пропорционален активности")
    void partialBonusIsProportional() {
        // 5 из 10 недель = 50% = floor(7.5) = 7
        assertEquals(7.0, tracker.calculateActivityBonus(5, cfg), 0.001);
    }

    @Test
    @DisplayName("null config → бонус 0")
    void nullConfigReturnsZero() {
        assertEquals(0.0, tracker.calculateActivityBonus(100, null), 0.001);
    }

    @Test
    @DisplayName("1 активная неделя из 10 → floor(1.5) = 1")
    void oneWeekOutOfTen() {
        assertEquals(1.0, tracker.calculateActivityBonus(1, cfg), 0.001);
    }
}

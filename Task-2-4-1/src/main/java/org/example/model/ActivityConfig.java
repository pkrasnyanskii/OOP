package org.example.model;

import lombok.Data;

import java.time.LocalDate;

/**
 * Конфигурация отслеживания активности студентов (дополнительное задание).
 *
 * Студент считается «активным» в неделю, если сделал ≥1 коммита в репозиторий.
 * Если за семестр activeWeeks >= minActiveWeeks → студент получает bonusPoints к итогу.
 */
@Data
public class ActivityConfig {
    private LocalDate courseStart;
    private LocalDate courseEnd;
    private int    minActiveWeeks = 10;
    private double bonusPoints    = 15.0;
}

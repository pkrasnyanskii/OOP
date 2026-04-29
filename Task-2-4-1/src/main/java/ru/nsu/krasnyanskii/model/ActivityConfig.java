package ru.nsu.krasnyanskii.model;

import lombok.Data;

import java.time.LocalDate;

/** Configuration for the weekly activity bonus task. */
@Data
public class ActivityConfig {
    private LocalDate courseStart;
    private LocalDate courseEnd;
    private int    minActiveWeeks = 10;
    private double bonusPoints    = 15.0;
}

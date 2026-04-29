package ru.nsu.krasnyanskii.model;

import lombok.Data;

/** Maps a score percentage to a 2–5 academic grade. */
@Data
public class GradeScale {
    private double excellent    = 90.0;
    private double good         = 75.0;
    private double satisfactory = 60.0;

    /**
     * Converts a percentage (0–100) to a grade: 5, 4, 3, or 2.
     *
     * @param scorePercent percentage of maximum score earned
     * @return grade from 2 (fail) to 5 (excellent)
     */
    public int toGrade(double scorePercent) {
        if (scorePercent >= excellent)    return 5;
        if (scorePercent >= good)         return 4;
        if (scorePercent >= satisfactory) return 3;
        return 2;
    }
}

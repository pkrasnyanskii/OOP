package org.example.model;

import lombok.Data;

/**
 * Шкала перевода процента набранных баллов в академическую оценку.
 *
 * Логика: если набрано >= excellent% → 5, >= good% → 4, >= satisfactory% → 3, иначе → 2.
 * Значения по умолчанию соответствуют типичным критериям курса ООП НГУ.
 */
@Data
public class GradeScale {
    private double excellent    = 90.0;
    private double good         = 75.0;
    private double satisfactory = 60.0;

    /**
     * Переводит процент (0–100) набранных баллов в оценку 2–5.
     */
    public int toGrade(double scorePercent) {
        if (scorePercent >= excellent)    return 5;
        if (scorePercent >= good)         return 4;
        if (scorePercent >= satisfactory) return 3;
        return 2;
    }
}

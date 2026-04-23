package org.example.model;

import java.time.LocalDate;

public class ActivityConfig {
    private LocalDate courseStart;
    private LocalDate courseEnd;
    private int minActiveWeeks = 10;
    private double bonusPoints = 15.0;

    public LocalDate getCourseStart() { return courseStart; }
    public void setCourseStart(LocalDate courseStart) { this.courseStart = courseStart; }

    public LocalDate getCourseEnd() { return courseEnd; }
    public void setCourseEnd(LocalDate courseEnd) { this.courseEnd = courseEnd; }

    public int getMinActiveWeeks() { return minActiveWeeks; }
    public void setMinActiveWeeks(int minActiveWeeks) { this.minActiveWeeks = minActiveWeeks; }

    public double getBonusPoints() { return bonusPoints; }
    public void setBonusPoints(double bonusPoints) { this.bonusPoints = bonusPoints; }
}

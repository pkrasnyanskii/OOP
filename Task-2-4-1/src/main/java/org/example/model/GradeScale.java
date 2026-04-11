package org.example.model;

public class GradeScale {
    private double excellent = 90.0;
    private double good = 75.0;
    private double satisfactory = 60.0;

    public double getExcellent() { return excellent; }
    public void setExcellent(double excellent) { this.excellent = excellent; }

    public double getGood() { return good; }
    public void setGood(double good) { this.good = good; }

    public double getSatisfactory() { return satisfactory; }
    public void setSatisfactory(double satisfactory) { this.satisfactory = satisfactory; }

    public int toGrade(double scorePercent) {
        if (scorePercent >= excellent) return 5;
        if (scorePercent >= good) return 4;
        if (scorePercent >= satisfactory) return 3;
        return 2;
    }
}

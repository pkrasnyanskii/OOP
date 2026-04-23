package org.example.model;

public class ScoringConfig {
    private int testTimeoutSeconds = 300;
    private double styleDeductionPercent = 10.0;
    private double docsDeductionPercent = 10.0;
    private double softDeadlinePenaltyPerDay = 1.0;
    private GradeScale gradeScale = new GradeScale();

    public int getTestTimeoutSeconds() { return testTimeoutSeconds; }
    public void setTestTimeoutSeconds(int testTimeoutSeconds) { this.testTimeoutSeconds = testTimeoutSeconds; }

    public double getStyleDeductionPercent() { return styleDeductionPercent; }
    public void setStyleDeductionPercent(double styleDeductionPercent) { this.styleDeductionPercent = styleDeductionPercent; }

    public double getDocsDeductionPercent() { return docsDeductionPercent; }
    public void setDocsDeductionPercent(double docsDeductionPercent) { this.docsDeductionPercent = docsDeductionPercent; }

    public double getSoftDeadlinePenaltyPerDay() { return softDeadlinePenaltyPerDay; }
    public void setSoftDeadlinePenaltyPerDay(double softDeadlinePenaltyPerDay) {
        this.softDeadlinePenaltyPerDay = softDeadlinePenaltyPerDay;
    }

    public GradeScale getGradeScale() { return gradeScale; }
    public void setGradeScale(GradeScale gradeScale) { this.gradeScale = gradeScale; }
}

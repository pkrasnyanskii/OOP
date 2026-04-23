package org.example.model.results;

import java.time.LocalDate;

public class TaskCheckResult {
    private final String taskId;

    private BuildStatus compileStatus = BuildStatus.NOT_CHECKED;
    private BuildStatus styleStatus = BuildStatus.NOT_CHECKED;
    private BuildStatus docsStatus = BuildStatus.NOT_CHECKED;
    private BuildStatus testStatus = BuildStatus.NOT_CHECKED;

    private TestCounts testCounts = new TestCounts();
    private LocalDate lastCommitDate;
    private double score = 0.0;

    private String compileOutput = "";
    private String styleOutput = "";
    private String docsOutput = "";
    private String testOutput = "";

    public TaskCheckResult(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskId() { return taskId; }

    public BuildStatus getCompileStatus() { return compileStatus; }
    public void setCompileStatus(BuildStatus compileStatus) { this.compileStatus = compileStatus; }

    public BuildStatus getStyleStatus() { return styleStatus; }
    public void setStyleStatus(BuildStatus styleStatus) { this.styleStatus = styleStatus; }

    public BuildStatus getDocsStatus() { return docsStatus; }
    public void setDocsStatus(BuildStatus docsStatus) { this.docsStatus = docsStatus; }

    public BuildStatus getTestStatus() { return testStatus; }
    public void setTestStatus(BuildStatus testStatus) { this.testStatus = testStatus; }

    public TestCounts getTestCounts() { return testCounts; }
    public void setTestCounts(TestCounts testCounts) { this.testCounts = testCounts; }

    public LocalDate getLastCommitDate() { return lastCommitDate; }
    public void setLastCommitDate(LocalDate lastCommitDate) { this.lastCommitDate = lastCommitDate; }

    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }

    public String getCompileOutput() { return compileOutput; }
    public void setCompileOutput(String compileOutput) { this.compileOutput = compileOutput; }

    public String getStyleOutput() { return styleOutput; }
    public void setStyleOutput(String styleOutput) { this.styleOutput = styleOutput; }

    public String getDocsOutput() { return docsOutput; }
    public void setDocsOutput(String docsOutput) { this.docsOutput = docsOutput; }

    public String getTestOutput() { return testOutput; }
    public void setTestOutput(String testOutput) { this.testOutput = testOutput; }
}

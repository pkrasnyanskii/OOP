package ru.nsu.krasnyanskii.checker;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.nsu.krasnyanskii.model.BonusEntry;
import ru.nsu.krasnyanskii.model.Group;
import ru.nsu.krasnyanskii.model.OopCheckerConfig;
import ru.nsu.krasnyanskii.model.Student;
import ru.nsu.krasnyanskii.model.Task;
import ru.nsu.krasnyanskii.model.results.BuildStatus;
import ru.nsu.krasnyanskii.model.results.TaskCheckResult;
import ru.nsu.krasnyanskii.model.results.TestCounts;

@DisplayName("ScoreCalculator — scoring logic")
class ScoreCalculatorTest {

    private OopCheckerConfig config;
    private ScoreCalculator  calculator;

    private static final String GITHUB  = "test-student";
    private static final String TASK_ID = "Task-1-1";

    @BeforeEach
    void setUp() {
        config = new OopCheckerConfig();

        LocalDate soft = LocalDate.of(2024, 3, 1);
        LocalDate hard = LocalDate.of(2024, 3, 15);
        Task task = new Task(TASK_ID, "Test Task", 100.0, soft, hard);
        config.addTask(task);

        Student student = new Student(GITHUB, "Test Student", "https://github.com/test/OOP");
        Group group = new Group("22201");
        group.addStudent(student);
        config.addGroup(group);

        calculator = new ScoreCalculator(config);
    }

    private TaskCheckResult successResult(int passed, int failed, int skipped) {
        TaskCheckResult r = new TaskCheckResult(TASK_ID);
        r.setCompileStatus(BuildStatus.SUCCESS);
        r.setDocsStatus(BuildStatus.SUCCESS);
        r.setStyleStatus(BuildStatus.SUCCESS);
        r.setTestStatus(failed == 0 ? BuildStatus.SUCCESS : BuildStatus.FAILED);
        r.setTestCounts(new TestCounts(passed, failed, skipped));
        r.setLastCommitDate(LocalDate.of(2024, 2, 20));
        return r;
    }

    @Test
    @DisplayName("Compile failed → 0 score")
    void compileFailed_zeroScore() {
        TaskCheckResult r = new TaskCheckResult(TASK_ID);
        r.setCompileStatus(BuildStatus.FAILED);
        assertEquals(0.0, calculator.calculate(GITHUB, r), 0.001);
    }

    @Test
    @DisplayName("Compile timeout → 0 score")
    void compileTimeout_zeroScore() {
        TaskCheckResult r = new TaskCheckResult(TASK_ID);
        r.setCompileStatus(BuildStatus.TIMEOUT);
        assertEquals(0.0, calculator.calculate(GITHUB, r), 0.001);
    }

    @Test
    @DisplayName("All tests passed → full score")
    void allTestsPassed_fullScore() {
        assertEquals(100.0, calculator.calculate(GITHUB, successResult(10, 0, 0)), 0.001);
    }

    @Test
    @DisplayName("Half tests passed → 50% score")
    void halfTestsPassed_halfScore() {
        assertEquals(50.0, calculator.calculate(GITHUB, successResult(5, 5, 0)), 0.001);
    }

    @Test
    @DisplayName("No tests → full score (compiled successfully)")
    void noTests_fullScore() {
        assertEquals(100.0, calculator.calculate(GITHUB, successResult(0, 0, 0)), 0.001);
    }

    @Test
    @DisplayName("Style failed → -10% of maxScore")
    void styleFailedDeduction() {
        TaskCheckResult r = successResult(10, 0, 0);
        r.setStyleStatus(BuildStatus.FAILED);
        assertEquals(90.0, calculator.calculate(GITHUB, r), 0.001);
    }

    @Test
    @DisplayName("Docs failed → -10% of maxScore")
    void docsFailedDeduction() {
        TaskCheckResult r = successResult(10, 0, 0);
        r.setDocsStatus(BuildStatus.FAILED);
        assertEquals(90.0, calculator.calculate(GITHUB, r), 0.001);
    }

    @Test
    @DisplayName("Style and docs both failed → -20%")
    void bothStyleAndDocsFailedDeduction() {
        TaskCheckResult r = successResult(10, 0, 0);
        r.setStyleStatus(BuildStatus.FAILED);
        r.setDocsStatus(BuildStatus.FAILED);
        assertEquals(80.0, calculator.calculate(GITHUB, r), 0.001);
    }

    @Test
    @DisplayName("Deductions cannot make score negative")
    void deductionFlooredAtZero() {
        config.getScoringConfig().setStyleDeductionPercent(60);
        config.getScoringConfig().setDocsDeductionPercent(60);
        TaskCheckResult r = successResult(10, 0, 0);
        r.setStyleStatus(BuildStatus.FAILED);
        r.setDocsStatus(BuildStatus.FAILED);
        assertEquals(0.0, calculator.calculate(GITHUB, r), 0.001);
    }

    @Test
    @DisplayName("After hard deadline → 0 score")
    void afterHardDeadline_zeroScore() {
        TaskCheckResult r = successResult(10, 0, 0);
        r.setLastCommitDate(LocalDate.of(2024, 3, 16));
        assertEquals(0.0, calculator.calculate(GITHUB, r), 0.001);
    }

    @Test
    @DisplayName("After soft deadline → 1 point penalty per day")
    void afterSoftDeadline_penaltyPerDay() {
        TaskCheckResult r = successResult(10, 0, 0);
        r.setLastCommitDate(LocalDate.of(2024, 3, 6)); // 5 days after soft deadline
        assertEquals(95.0, calculator.calculate(GITHUB, r), 0.001);
    }

    @Test
    @DisplayName("On soft deadline → no penalty")
    void onSoftDeadline_noPenalty() {
        TaskCheckResult r = successResult(10, 0, 0);
        r.setLastCommitDate(LocalDate.of(2024, 3, 1));
        assertEquals(100.0, calculator.calculate(GITHUB, r), 0.001);
    }

    @Test
    @DisplayName("Bonus is added to score")
    void bonusAdded() {
        config.addBonusEntry(new BonusEntry(GITHUB, TASK_ID, 5.0));
        TaskCheckResult r = successResult(10, 0, 0);
        // 100 + 5, but capped at maxScore=100
        assertEquals(100.0, calculator.calculate(GITHUB, r), 0.001);
    }

    @Test
    @DisplayName("Bonus cannot exceed maxScore")
    void bonusCappedAtMaxScore() {
        config.addBonusEntry(new BonusEntry(GITHUB, TASK_ID, 50.0));
        TaskCheckResult r = successResult(5, 5, 0); // base = 50
        // 50 + 50 = 100, not 105
        assertEquals(100.0, calculator.calculate(GITHUB, r), 0.001);
    }
}

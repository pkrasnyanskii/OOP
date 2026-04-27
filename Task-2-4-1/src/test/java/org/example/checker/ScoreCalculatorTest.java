package org.example.checker;

import org.example.model.*;
import org.example.model.results.BuildStatus;
import org.example.model.results.TaskCheckResult;
import org.example.model.results.TestCounts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты ScoreCalculator — ядро логики выставления баллов по ТЗ:
 *   • Компиляция упала → 0
 *   • Балл пропорционален прошедшим тестам
 *   • Штраф за стиль/документацию
 *   • Штраф за опоздание после softDeadline
 *   • 0 баллов после hardDeadline
 *   • Бонусные баллы ограничены maxScore
 */
@DisplayName("ScoreCalculator — логика выставления баллов")
class ScoreCalculatorTest {

    private OopCheckerConfig config;
    private ScoreCalculator  calculator;
    private Task             task;

    private static final String GITHUB  = "test-student";
    private static final String TASK_ID = "Task-1-1";

    @BeforeEach
    void setUp() {
        config = new OopCheckerConfig();

        LocalDate soft = LocalDate.of(2024, 3, 1);
        LocalDate hard = LocalDate.of(2024, 3, 15);
        task = new Task(TASK_ID, "Test Task", 100.0, soft, hard);
        config.addTask(task);

        Student student = new Student(GITHUB, "Тестовый Студент", "https://github.com/test/OOP");
        Group group = new Group("22201");
        group.addStudent(student);
        config.addGroup(group);

        calculator = new ScoreCalculator(config);
    }

    /** Создаёт полностью успешный результат с указанными счётчиками тестов. */
    private TaskCheckResult successResult(int passed, int failed, int skipped) {
        TaskCheckResult r = new TaskCheckResult(TASK_ID);
        r.setCompileStatus(BuildStatus.SUCCESS);
        r.setDocsStatus(BuildStatus.SUCCESS);
        r.setStyleStatus(BuildStatus.SUCCESS);
        r.setTestStatus(failed == 0 ? BuildStatus.SUCCESS : BuildStatus.FAILED);
        r.setTestCounts(new TestCounts(passed, failed, skipped));
        r.setLastCommitDate(LocalDate.of(2024, 2, 20)); // до softDeadline
        return r;
    }

    // ── Компиляция ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("Компиляция упала → 0 баллов")
    void compileFailed_zeroScore() {
        TaskCheckResult r = new TaskCheckResult(TASK_ID);
        r.setCompileStatus(BuildStatus.FAILED);
        assertEquals(0.0, calculator.calculate(GITHUB, r), 0.001);
    }

    @Test
    @DisplayName("Таймаут компиляции → 0 баллов")
    void compileTimeout_zeroScore() {
        TaskCheckResult r = new TaskCheckResult(TASK_ID);
        r.setCompileStatus(BuildStatus.TIMEOUT);
        assertEquals(0.0, calculator.calculate(GITHUB, r), 0.001);
    }

    // ── Тесты ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Все тесты прошли → полный балл")
    void allTestsPassed_fullScore() {
        assertEquals(100.0, calculator.calculate(GITHUB, successResult(10, 0, 0)), 0.001);
    }

    @Test
    @DisplayName("Половина тестов прошла → 50% балла")
    void halfTestsPassed_halfScore() {
        assertEquals(50.0, calculator.calculate(GITHUB, successResult(5, 5, 0)), 0.001);
    }

    @Test
    @DisplayName("Нет тестов совсем → полный балл (скомпилировалось)")
    void noTests_fullScore() {
        assertEquals(100.0, calculator.calculate(GITHUB, successResult(0, 0, 0)), 0.001);
    }

    // ── Штрафы за стиль и документацию ────────────────────────────────────

    @Test
    @DisplayName("Стиль не прошёл → -10% от maxScore")
    void styleFailedDeduction() {
        TaskCheckResult r = successResult(10, 0, 0);
        r.setStyleStatus(BuildStatus.FAILED);
        // 100 - 10% = 90
        assertEquals(90.0, calculator.calculate(GITHUB, r), 0.001);
    }

    @Test
    @DisplayName("Документация не сгенерирована → -10% от maxScore")
    void docsFailedDeduction() {
        TaskCheckResult r = successResult(10, 0, 0);
        r.setDocsStatus(BuildStatus.FAILED);
        assertEquals(90.0, calculator.calculate(GITHUB, r), 0.001);
    }

    @Test
    @DisplayName("Стиль И документация упали → -20%, минимум 0")
    void bothStyleAndDocsFailedDeduction() {
        TaskCheckResult r = successResult(10, 0, 0);
        r.setStyleStatus(BuildStatus.FAILED);
        r.setDocsStatus(BuildStatus.FAILED);
        assertEquals(80.0, calculator.calculate(GITHUB, r), 0.001);
    }

    @Test
    @DisplayName("Штраф не уходит в минус (ограничен 0)")
    void deductionFlooredAtZero() {
        config.getScoringConfig().setStyleDeductionPercent(60);
        config.getScoringConfig().setDocsDeductionPercent(60);
        TaskCheckResult r = successResult(10, 0, 0);
        r.setStyleStatus(BuildStatus.FAILED);
        r.setDocsStatus(BuildStatus.FAILED);
        assertEquals(0.0, calculator.calculate(GITHUB, r), 0.001);
    }

    // ── Дедлайны ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("После hardDeadline → 0 баллов")
    void afterHardDeadline_zeroScore() {
        TaskCheckResult r = successResult(10, 0, 0);
        r.setLastCommitDate(LocalDate.of(2024, 3, 16)); // на день после hardDeadline
        assertEquals(0.0, calculator.calculate(GITHUB, r), 0.001);
    }

    @Test
    @DisplayName("После softDeadline — штраф 1 балл/день (по умолчанию)")
    void afterSoftDeadline_penaltyPerDay() {
        TaskCheckResult r = successResult(10, 0, 0);
        r.setLastCommitDate(LocalDate.of(2024, 3, 6)); // 5 дней после softDeadline (1 марта)
        // 100 - 5*1 = 95
        assertEquals(95.0, calculator.calculate(GITHUB, r), 0.001);
    }

    @Test
    @DisplayName("В день softDeadline — без штрафа")
    void onSoftDeadline_nopenalty() {
        TaskCheckResult r = successResult(10, 0, 0);
        r.setLastCommitDate(LocalDate.of(2024, 3, 1)); // ровно softDeadline
        assertEquals(100.0, calculator.calculate(GITHUB, r), 0.001);
    }

    // ── Бонусы ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Бонус добавляется к баллу")
    void bonusAdded() {
        config.addBonusEntry(new BonusEntry(GITHUB, TASK_ID, 5.0));
        TaskCheckResult r = successResult(10, 0, 0);
        // 100 (из тестов) + 5 (бонус), но ограничено maxScore=100
        assertEquals(100.0, calculator.calculate(GITHUB, r), 0.001);
    }

    @Test
    @DisplayName("Бонус не превышает maxScore")
    void bonusCappedAtMaxScore() {
        config.addBonusEntry(new BonusEntry(GITHUB, TASK_ID, 50.0));
        TaskCheckResult r = successResult(5, 5, 0); // базовый балл = 50
        // 50 + 50 = 100 (не 105)
        assertEquals(100.0, calculator.calculate(GITHUB, r), 0.001);
    }
}

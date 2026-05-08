package ru.nsu.krasnyanskii.report;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.nsu.krasnyanskii.model.ActivityConfig;
import ru.nsu.krasnyanskii.model.CheckInstruction;
import ru.nsu.krasnyanskii.model.CheckPoint;
import ru.nsu.krasnyanskii.model.Group;

import ru.nsu.krasnyanskii.model.OopCheckerConfig;
import ru.nsu.krasnyanskii.model.Student;
import ru.nsu.krasnyanskii.model.Task;
import ru.nsu.krasnyanskii.model.results.BuildStatus;
import ru.nsu.krasnyanskii.model.results.StudentCheckResult;
import ru.nsu.krasnyanskii.model.results.TaskCheckResult;
import ru.nsu.krasnyanskii.model.results.TestCounts;

@DisplayName("HtmlReporter — генерация HTML-отчёта")
class HtmlReporterTest {

    private static final String TASK1 = "Task-1-1";
    private static final String TASK2 = "Task-1-2";
    private static final String GH1   = "student1";
    private static final String GH2   = "student2";

    private OopCheckerConfig config;

    @BeforeEach
    void setUp() {
        config = new OopCheckerConfig();
        config.addTask(new Task(TASK1, "Hello World", 100.0, null, null));
        config.addTask(new Task(TASK2, "Fractions",   150.0,
                LocalDate.of(2024, 3, 1), LocalDate.of(2024, 3, 15)));

        Student s1 = new Student(GH1, "Иван Иванов", "https://github.com/s1/OOP");
        Student s2 = new Student(GH2, "Пётр Петров", "https://github.com/s2/OOP");
        Group g = new Group("22201");
        g.addStudent(s1);
        g.addStudent(s2);
        config.addGroup(g);

        CheckInstruction instr = new CheckInstruction();
        instr.getStudentGithubs().add(GH1);
        instr.getStudentGithubs().add(GH2);
        instr.getTaskIds().add(TASK1);
        instr.getTaskIds().add(TASK2);
        config.setCheckInstruction(instr);
    }

    private TaskCheckResult successResult(String taskId, int passed, int failed) {
        TaskCheckResult r = new TaskCheckResult(taskId);
        r.setCompileStatus(BuildStatus.SUCCESS);
        r.setDocsStatus(BuildStatus.SUCCESS);
        r.setStyleStatus(BuildStatus.SUCCESS);
        r.setTestCounts(new TestCounts(passed, failed, 0));
        r.setTestStatus(failed == 0 ? BuildStatus.SUCCESS : BuildStatus.FAILED);
        r.setScore(failed == 0 ? 100.0 : (double) passed / (passed + failed) * 100);
        r.setLastCommitDate(LocalDate.of(2024, 2, 20));
        return r;
    }

    private TaskCheckResult failedResult(String taskId) {
        TaskCheckResult r = new TaskCheckResult(taskId);
        r.setCompileStatus(BuildStatus.FAILED);
        r.setScore(0.0);
        return r;
    }

    private TaskCheckResult timeoutResult(String taskId) {
        TaskCheckResult r = new TaskCheckResult(taskId);
        r.setCompileStatus(BuildStatus.TIMEOUT);
        r.setScore(0.0);
        return r;
    }

    private TaskCheckResult notCheckedResult(String taskId) {
        TaskCheckResult r = new TaskCheckResult(taskId);
        r.setScore(0.0);
        return r;
    }

    private StudentCheckResult makeStudentResult(String github, TaskCheckResult... tasks) {
        StudentCheckResult sr = new StudentCheckResult(github, "Name " + github, "22201");
        for (TaskCheckResult tr : tasks) {
            sr.addTaskResult(tr);
        }
        return sr;
    }

    @Test
    @DisplayName("generate() возвращает валидный HTML")
    void generateBasicHtml() {
        HtmlReporter reporter = new HtmlReporter(config);
        StudentCheckResult sr = makeStudentResult(GH1,
                successResult(TASK1, 10, 0),
                successResult(TASK2, 5, 0));

        String html = reporter.generate(List.of(sr));

        assertNotNull(html);
        assertTrue(html.contains("<!DOCTYPE html>"));
        assertTrue(html.contains("OOP Check Report"));
        assertTrue(html.contains("Иван Иванов") || html.contains("student1"));
    }

    @Test
    @DisplayName("generate() включает имена задач и максимальный балл")
    void generateIncludesTaskInfo() {
        HtmlReporter reporter = new HtmlReporter(config);
        StudentCheckResult sr = makeStudentResult(GH1, successResult(TASK1, 10, 0));

        String html = reporter.generate(List.of(sr));

        assertTrue(html.contains("Hello World"));
        assertTrue(html.contains("Task-1-1"));
    }

    @Test
    @DisplayName("generate() корректно рендерит ошибку компиляции")
    void renderCompileFailed() {
        HtmlReporter reporter = new HtmlReporter(config);
        StudentCheckResult sr = makeStudentResult(GH1, failedResult(TASK1));

        String html = reporter.generate(List.of(sr));

        assertTrue(html.contains("Ошибка компиляции"));
    }

    @Test
    @DisplayName("generate() корректно рендерит таймаут компиляции")
    void renderCompileTimeout() {
        HtmlReporter reporter = new HtmlReporter(config);
        StudentCheckResult sr = makeStudentResult(GH1, timeoutResult(TASK1));

        String html = reporter.generate(List.of(sr));

        assertTrue(html.contains("Таймаут компиляции"));
    }

    @Test
    @DisplayName("generate() корректно рендерит NOT_CHECKED")
    void renderNotChecked() {
        HtmlReporter reporter = new HtmlReporter(config);
        StudentCheckResult sr = makeStudentResult(GH1, notCheckedResult(TASK1));

        String html = reporter.generate(List.of(sr));

        assertTrue(html.contains("Не проверялось"));
    }

    @Test
    @DisplayName("generate() рендерит НД когда нет результата для задачи")
    void renderMissingTask() {
        HtmlReporter reporter = new HtmlReporter(config);
        StudentCheckResult sr = makeStudentResult(GH1, successResult(TASK1, 5, 0));

        String html = reporter.generate(List.of(sr));

        assertTrue(html.contains("—"));
    }

    @Test
    @DisplayName("generate() включает контрольные точки когда они есть")
    void generateWithCheckPoints() {
        CheckPoint cp = new CheckPoint("КТ-1", LocalDate.of(2024, 3, 26));
        cp.addTaskId(TASK1);
        config.addCheckPoint(cp);

        HtmlReporter reporter = new HtmlReporter(config);
        StudentCheckResult sr = makeStudentResult(GH1,
                successResult(TASK1, 8, 0),
                successResult(TASK2, 5, 0));

        String html = reporter.generate(List.of(sr));

        assertTrue(html.contains("Контрольные точки"));
        assertTrue(html.contains("КТ-1"));
    }

    @Test
    @DisplayName("generate() включает таблицу активности когда activityConfig задан")
    void generateWithActivityConfig() {
        ActivityConfig ac = new ActivityConfig();
        ac.setCourseStart(LocalDate.of(2024, 2, 1));
        ac.setCourseEnd(LocalDate.of(2024, 6, 30));
        ac.setMinActiveWeeks(10);
        ac.setBonusPoints(15.0);
        config.setActivityConfig(ac);

        HtmlReporter reporter = new HtmlReporter(config);
        StudentCheckResult sr = makeStudentResult(GH1, successResult(TASK1, 5, 0));
        sr.setActiveWeeks(12);
        sr.setActivityBonus(15.0);

        String html = reporter.generate(List.of(sr));

        assertTrue(html.contains("Активность студентов"));
        assertTrue(html.contains("15"));
    }

    @Test
    @DisplayName("generate() с activityBonus > 0 показывает бонус в итогах")
    void generateActivityBonusInTotal() {
        HtmlReporter reporter = new HtmlReporter(config);
        StudentCheckResult sr = makeStudentResult(GH1, successResult(TASK1, 5, 0));
        sr.setActivityBonus(10.0);

        String html = reporter.generate(List.of(sr));

        assertTrue(html.contains("активность"));
    }

    @Test
    @DisplayName("generate() без ActivityConfig не включает таблицу активности")
    void generateWithoutActivityConfig() {
        HtmlReporter reporter = new HtmlReporter(config);
        StudentCheckResult sr = makeStudentResult(GH1, successResult(TASK1, 5, 0));

        String html = reporter.generate(List.of(sr));

        assertFalse(html.contains("Активность студентов"));
    }

    @Test
    @DisplayName("generate() корректно экранирует спецсимволы HTML")
    void htmlEscaping() {
        config.addTask(new Task("Task-&lt;X&gt;", "Task & Test", 50.0, null, null));
        CheckInstruction instr = config.getCheckInstruction();
        instr.getTaskIds().add("Task-&lt;X&gt;");

        HtmlReporter reporter = new HtmlReporter(config);
        StudentCheckResult sr = new StudentCheckResult(GH1, "Student <b>Name</b>", "22201");
        sr.addTaskResult(successResult(TASK1, 3, 0));

        String html = reporter.generate(List.of(sr));

        assertFalse(html.contains("<b>Name</b>"));
        assertTrue(html.contains("&lt;") || !html.contains("<b>"));
    }

    @Test
    @DisplayName("generate() рендерит итоговые оценки для всех студентов")
    void generateFinalGrades() {
        HtmlReporter reporter = new HtmlReporter(config);
        StudentCheckResult sr1 = makeStudentResult(GH1,
                successResult(TASK1, 10, 0),
                successResult(TASK2, 10, 0));
        sr1.addTaskResult(new TaskCheckResult(TASK1));
        StudentCheckResult sr2 = makeStudentResult(GH2, failedResult(TASK1));

        String html = reporter.generate(List.of(sr1, sr2));

        assertTrue(html.contains("Итоговые оценки"));
        assertTrue(html.matches("(?s).*[2-5].*"));
    }

    @Test
    @DisplayName("generate() включает легенду со шкалой оценок")
    void generateLegend() {
        HtmlReporter reporter = new HtmlReporter(config);
        String html = reporter.generate(List.of());

        assertTrue(html.contains("Легенда"));
        assertTrue(html.contains("отлично") || html.contains("90") || html.contains("%"));
    }

    @Test
    @DisplayName("generate() с NOT_AVAILABLE для style/docs рендерит прочерк")
    void renderStyleDocsNotAvailable() {
        HtmlReporter reporter = new HtmlReporter(config);

        TaskCheckResult tr = new TaskCheckResult(TASK1);
        tr.setCompileStatus(BuildStatus.SUCCESS);
        tr.setDocsStatus(BuildStatus.NOT_AVAILABLE);
        tr.setStyleStatus(BuildStatus.NOT_AVAILABLE);
        tr.setTestCounts(new TestCounts(5, 0, 0));
        tr.setTestStatus(BuildStatus.SUCCESS);
        tr.setScore(100.0);

        StudentCheckResult sr = makeStudentResult(GH1, tr);
        String html = reporter.generate(List.of(sr));

        assertTrue(html.contains("—"));
    }
}

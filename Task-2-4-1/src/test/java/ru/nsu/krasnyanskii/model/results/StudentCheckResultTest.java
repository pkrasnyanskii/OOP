package ru.nsu.krasnyanskii.model.results;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("StudentCheckResult — поля и агрегация")
class StudentCheckResultTest {

    @Test
    @DisplayName("Конструктор сохраняет поля")
    void constructor() {
        StudentCheckResult r = new StudentCheckResult("user1", "Иван Иванов", "22201");
        assertEquals("user1",        r.getStudentGithub());
        assertEquals("Иван Иванов",  r.getStudentName());
        assertEquals("22201",        r.getGroupName());
        assertTrue(r.getTaskResults().isEmpty());
    }

    @Test
    @DisplayName("addTaskResult и getTaskResult работают корректно")
    void addAndGetTaskResult() {
        StudentCheckResult sr = new StudentCheckResult("u", "N", "G");
        TaskCheckResult    tr = new TaskCheckResult("Task-1-1");
        tr.setScore(80.0);

        sr.addTaskResult(tr);

        assertTrue(sr.getTaskResult("Task-1-1").isPresent());
        assertEquals(80.0, sr.getTaskResult("Task-1-1").get().getScore(), 0.001);
        assertFalse(sr.getTaskResult("Task-X").isPresent());
    }

    @Test
    @DisplayName("getTotalScore суммирует баллы за задачи + activity bonus")
    void getTotalScore() {
        StudentCheckResult sr = new StudentCheckResult("u", "N", "G");

        TaskCheckResult t1 = new TaskCheckResult("T1");
        t1.setScore(50.0);
        TaskCheckResult t2 = new TaskCheckResult("T2");
        t2.setScore(30.0);

        sr.addTaskResult(t1);
        sr.addTaskResult(t2);
        sr.setActivityBonus(10.0);

        assertEquals(90.0, sr.getTotalScore(), 0.001);
    }

    @Test
    @DisplayName("getTotalScore без задач и без бонуса = 0")
    void getTotalScore_empty() {
        StudentCheckResult sr = new StudentCheckResult("u", "N", "G");
        assertEquals(0.0, sr.getTotalScore(), 0.001);
    }

    @Test
    @DisplayName("activeWeeks и activityBonus сеттеры работают")
    void settersWork() {
        StudentCheckResult sr = new StudentCheckResult("u", "N", "G");
        sr.setActiveWeeks(7);
        sr.setActivityBonus(5.5);
        assertEquals(7,   sr.getActiveWeeks());
        assertEquals(5.5, sr.getActivityBonus(), 0.001);
    }
}

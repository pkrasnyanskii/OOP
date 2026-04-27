package org.example.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OopCheckerConfig — поиск по идентификаторам")
class OopCheckerConfigTest {

    private OopCheckerConfig config;

    @BeforeEach
    void setUp() {
        config = new OopCheckerConfig();

        Task task = new Task("Task-1-1", "Hello World", 100, null, null);
        config.addTask(task);

        Student student = new Student("ivanov", "Иванов И.И.", "https://github.com/ivanov/OOP");
        Group group = new Group("22201");
        group.addStudent(student);
        config.addGroup(group);

        config.addBonusEntry(new BonusEntry("ivanov", "Task-1-1", 5.0));
        config.addBonusEntry(new BonusEntry("ivanov", "Task-1-1", 3.0)); // второй бонус суммируется
    }

    @Test
    @DisplayName("findTaskById находит существующую задачу")
    void findExistingTask() {
        assertTrue(config.findTaskById("Task-1-1").isPresent());
        assertEquals("Hello World", config.findTaskById("Task-1-1").get().getName());
    }

    @Test
    @DisplayName("findTaskById возвращает empty для несуществующей задачи")
    void findMissingTask() {
        assertTrue(config.findTaskById("Task-99-99").isEmpty());
    }

    @Test
    @DisplayName("findStudentByGithub находит студента")
    void findExistingStudent() {
        assertTrue(config.findStudentByGithub("ivanov").isPresent());
        assertEquals("Иванов И.И.", config.findStudentByGithub("ivanov").get().getFullName());
    }

    @Test
    @DisplayName("findStudentByGithub возвращает empty для неизвестного ника")
    void findMissingStudent() {
        assertTrue(config.findStudentByGithub("unknown-user").isEmpty());
    }

    @Test
    @DisplayName("findGroupByStudentGithub возвращает группу студента")
    void findGroupForStudent() {
        var group = config.findGroupByStudentGithub("ivanov");
        assertTrue(group.isPresent());
        assertEquals("22201", group.get().getName());
    }

    @Test
    @DisplayName("getBonusFor суммирует все бонусы студента за задачу")
    void bonusEntriesAreSummed() {
        assertEquals(8.0, config.getBonusFor("ivanov", "Task-1-1"), 0.001);
    }

    @Test
    @DisplayName("getBonusFor возвращает 0 если бонусов нет")
    void noBonusReturnsZero() {
        assertEquals(0.0, config.getBonusFor("ivanov", "Task-2-1"), 0.001);
        assertEquals(0.0, config.getBonusFor("unknown", "Task-1-1"), 0.001);
    }
}

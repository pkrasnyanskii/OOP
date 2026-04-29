package ru.nsu.krasnyanskii.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OopCheckerConfig — lookup methods")
class OopCheckerConfigTest {

    private OopCheckerConfig config;

    @BeforeEach
    void setUp() {
        config = new OopCheckerConfig();

        Task task = new Task("Task-1-1", "Hello World", 100, null, null);
        config.addTask(task);

        Student student = new Student("ivanov", "Ivanov I.I.", "https://github.com/ivanov/OOP");
        Group group = new Group("22201");
        group.addStudent(student);
        config.addGroup(group);

        config.addBonusEntry(new BonusEntry("ivanov", "Task-1-1", 5.0));
        config.addBonusEntry(new BonusEntry("ivanov", "Task-1-1", 3.0)); // second bonus sums
    }

    @Test
    @DisplayName("findTaskById finds an existing task")
    void findExistingTask() {
        assertTrue(config.findTaskById("Task-1-1").isPresent());
        assertEquals("Hello World", config.findTaskById("Task-1-1").get().getName());
    }

    @Test
    @DisplayName("findTaskById returns empty for unknown id")
    void findMissingTask() {
        assertTrue(config.findTaskById("Task-99-99").isEmpty());
    }

    @Test
    @DisplayName("findStudentByGithub finds an existing student")
    void findExistingStudent() {
        assertTrue(config.findStudentByGithub("ivanov").isPresent());
        assertEquals("Ivanov I.I.", config.findStudentByGithub("ivanov").get().getFullName());
    }

    @Test
    @DisplayName("findStudentByGithub returns empty for unknown login")
    void findMissingStudent() {
        assertTrue(config.findStudentByGithub("unknown-user").isEmpty());
    }

    @Test
    @DisplayName("findGroupByStudentGithub returns the student's group")
    void findGroupForStudent() {
        var group = config.findGroupByStudentGithub("ivanov");
        assertTrue(group.isPresent());
        assertEquals("22201", group.get().getName());
    }

    @Test
    @DisplayName("getBonusFor sums all bonus entries for a student+task pair")
    void bonusEntriesAreSummed() {
        assertEquals(8.0, config.getBonusFor("ivanov", "Task-1-1"), 0.001);
    }

    @Test
    @DisplayName("getBonusFor returns 0 when no bonuses exist")
    void noBonusReturnsZero() {
        assertEquals(0.0, config.getBonusFor("ivanov", "Task-2-1"), 0.001);
        assertEquals(0.0, config.getBonusFor("unknown", "Task-1-1"), 0.001);
    }
}

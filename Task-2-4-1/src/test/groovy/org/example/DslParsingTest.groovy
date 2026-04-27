package org.example

import org.codehaus.groovy.control.CompilerConfiguration
import org.example.config.ConfigLoader
import org.example.dsl.OopCheckerScriptInterface
import org.example.model.OopCheckerConfig
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

import java.nio.file.Path

import static org.junit.jupiter.api.Assertions.*

/**
 * Тесты DSL-парсинга.
 * Проверяют, что Groovy DSL-скрипты корректно заполняют объект OopCheckerConfig.
 */
@DisplayName("DSL — парсинг конфигурационных файлов")
class DslParsingTest {

    @TempDir
    Path tempDir

    /** Записывает содержимое в файл и загружает его как конфиг. */
    private OopCheckerConfig parseScript(String content) {
        def file = tempDir.resolve("oop_checker.groovy").toFile()
        file.text = content
        return ConfigLoader.loadFromFile(file)
    }

    // ── Задачи ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("tasks {} заполняет список задач")
    void tasksParsedCorrectly() {
        def config = parseScript("""
            tasks {
                task("Task-1-1") {
                    name         = "Hello World"
                    maxScore     = 100
                    softDeadline = "2024-03-01"
                    hardDeadline = "2024-03-15"
                }
                task("Task-1-2") {
                    name         = "Fractions"
                    maxScore     = 150
                }
            }
        """)

        assertEquals(2, config.tasks.size())

        def t1 = config.findTaskById("Task-1-1").get()
        assertEquals("Hello World", t1.name)
        assertEquals(100.0, t1.maxScore, 0.001)
        assertNotNull(t1.softDeadline)
        assertNotNull(t1.hardDeadline)

        def t2 = config.findTaskById("Task-1-2").get()
        assertEquals(150.0, t2.maxScore, 0.001)
        assertNull(t2.softDeadline)
    }

    // ── Группы и студенты ──────────────────────────────────────────────────

    @Test
    @DisplayName("groups {} с несколькими студентами парсится корректно")
    void groupsParsedCorrectly() {
        def config = parseScript("""
            groups {
                group("22201") {
                    student {
                        github = "ivanov"
                        name   = "Иванов И.И."
                        repo   = "https://github.com/ivanov/OOP"
                    }
                    student {
                        github = "petrov"
                        name   = "Петров П.П."
                        repo   = "https://github.com/petrov/OOP"
                    }
                }
                group("22202") {
                    student {
                        github = "sidorov"
                        name   = "Сидоров С.С."
                        repo   = "https://github.com/sidorov/OOP"
                    }
                }
            }
        """)

        assertEquals(2, config.groups.size())

        def g1 = config.groups[0]
        assertEquals("22201", g1.name)
        assertEquals(2, g1.students.size())
        assertEquals("Иванов И.И.", config.findStudentByGithub("ivanov").get().fullName)

        def g2 = config.groups[1]
        assertEquals(1, g2.students.size())
    }

    // ── Check + Checkpoints ────────────────────────────────────────────────

    @Test
    @DisplayName("check {} и checkPoints {} парсятся корректно")
    void checkAndCheckPointsParsed() {
        def config = parseScript("""
            check {
                students "ivanov", "petrov"
                tasks    "Task-1-1", "Task-1-2"
            }
            checkPoints {
                checkPoint("КТ-1") {
                    date    = "2024-04-01"
                    tasks   = ["Task-1-1"]
                    isFinal = false
                }
                checkPoint("Итог") {
                    date    = "2024-06-20"
                    tasks   = ["Task-1-1", "Task-1-2"]
                    isFinal = true
                }
            }
        """)

        assertEquals(["ivanov", "petrov"], config.checkInstruction.studentGithubs)
        assertEquals(["Task-1-1", "Task-1-2"], config.checkInstruction.taskIds)

        assertEquals(2, config.checkPoints.size())
        def cp2 = config.checkPoints[1]
        assertEquals("Итог", cp2.name)
        assertTrue(cp2.isFinal())
        assertEquals(2, cp2.taskIds.size())
    }

    // ── Settings ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("settings {} заполняет ScoringConfig и GradeScale")
    void settingsParsedCorrectly() {
        def config = parseScript("""
            settings {
                testTimeout               = 120
                styleDeduction            = 15
                docsDeduction             = 5
                softDeadlinePenaltyPerDay = 2.0
                gradeScale {
                    excellent    = 95
                    good         = 80
                    satisfactory = 65
                }
            }
        """)

        def sc = config.scoringConfig
        assertEquals(120, sc.testTimeoutSeconds)
        assertEquals(15.0, sc.styleDeductionPercent, 0.001)
        assertEquals(5.0,  sc.docsDeductionPercent,  0.001)
        assertEquals(2.0,  sc.softDeadlinePenaltyPerDay, 0.001)
        assertEquals(95.0, sc.gradeScale.excellent,    0.001)
        assertEquals(80.0, sc.gradeScale.good,         0.001)
        assertEquals(65.0, sc.gradeScale.satisfactory, 0.001)
    }

    // ── Бонусы ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("bonus {} создаёт BonusEntry записи")
    void bonusParsedCorrectly() {
        def config = parseScript("""
            settings {
                bonus {
                    student "ivanov" task "Task-1-1" points 5
                    student "petrov" task "Task-1-2" points 10
                }
            }
        """)

        assertEquals(2, config.bonusEntries.size())
        assertEquals(5.0,  config.getBonusFor("ivanov", "Task-1-1"), 0.001)
        assertEquals(10.0, config.getBonusFor("petrov", "Task-1-2"), 0.001)
        assertEquals(0.0,  config.getBonusFor("ivanov", "Task-1-2"), 0.001)
    }

    // ── Активность (доп. задание) ───────────────────────────────────────────

    @Test
    @DisplayName("activity {} создаёт ActivityConfig")
    void activityParsedCorrectly() {
        def config = parseScript("""
            settings {
                activity {
                    courseStart    = "2024-02-01"
                    courseEnd      = "2024-06-30"
                    minActiveWeeks = 12
                    bonusPoints    = 20
                }
            }
        """)

        def ac = config.activityConfig
        assertNotNull(ac)
        assertEquals(12,   ac.minActiveWeeks)
        assertEquals(20.0, ac.bonusPoints, 0.001)
        assertEquals("2024-02-01", ac.courseStart.toString())
    }

    // ── importConfig ────────────────────────────────────────────────────────

    @Test
    @DisplayName("importConfig загружает задачи из вложенного файла")
    void importConfigMergesTasksFromSubFile(@TempDir Path dir) {
        // Создаём вложенный файл
        def tasksFile = dir.resolve("tasks.groovy").toFile()
        tasksFile.text = """
            tasks {
                task("Task-X") {
                    name     = "Imported Task"
                    maxScore = 50
                }
            }
        """
        // Основной файл импортирует вложенный
        def mainFile = dir.resolve("oop_checker.groovy").toFile()
        mainFile.text = """
            importConfig "tasks.groovy"
            tasks {
                task("Task-Y") {
                    name     = "Local Task"
                    maxScore = 100
                }
            }
        """

        def config = ConfigLoader.loadFromFile(mainFile)
        assertEquals(2, config.tasks.size())
        assertTrue(config.findTaskById("Task-X").isPresent())
        assertTrue(config.findTaskById("Task-Y").isPresent())
    }
}

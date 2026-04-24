// ═══════════════════════════════════════════════════════════════════════════
// oop_checker.groovy — корневой конфиг для запуска из корня проекта.
//
// Приложение ищет этот файл в рабочей директории (как Gradle ищет build.gradle).
// При запуске из IntelliJ рабочая директория = Task-2-4-1/, поэтому этот файл
// подхватывается автоматически.
//
// Для настоящего использования замените студентов и задачи на реальные данные.
// Разделение по "сроку жизни":
//   tasks.groovy    — редко меняется (список задач курса)
//   students.groovy — меняется раз в семестр (состав группы)
//   этот файл       — меняется перед каждой проверкой (кого и что проверять)
// ═══════════════════════════════════════════════════════════════════════════

importConfig "example_configs/tasks.groovy"
importConfig "example_configs/students.groovy"

check {
    students "ivanov-ivan", "petrov-petr", "sidorova-anna", "kozlov-dmitry", "novikova-elena"
    tasks    "Task-1-1", "Task-1-2", "Task-2-1", "Task-2-2"
}

checkPoints {
    checkPoint("КТ-1") {
        date  = "2024-03-26"
        tasks = ["Task-1-1", "Task-1-2"]
    }
    checkPoint("КТ-2") {
        date  = "2024-04-30"
        tasks = ["Task-1-1", "Task-1-2", "Task-2-1", "Task-2-2"]
    }
    checkPoint("Итог") {
        date    = "2024-06-20"
        tasks   = ["Task-1-1", "Task-1-2", "Task-2-1", "Task-2-2"]
        isFinal = true
    }
}

settings {
    testTimeout               = 120
    styleDeduction            = 10
    docsDeduction             = 10
    softDeadlinePenaltyPerDay = 1.0

    gradeScale {
        excellent    = 90
        good         = 75
        satisfactory = 60
    }

    bonus {
        student "ivanov-ivan"    task "Task-1-2" points 5
        student "novikova-elena" task "Task-2-1" points 10
    }

    activity {
        courseStart    = "2024-02-05"
        courseEnd      = "2024-06-20"
        minActiveWeeks = 10
        bonusPoints    = 15
    }
}

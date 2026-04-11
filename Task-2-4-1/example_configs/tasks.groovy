// ─────────────────────────────────────────────────────────────────────────────
// tasks.groovy  —  список задач курса ООП
// Этот файл меняется редко (один раз в семестр при смене курса).
// Импортируется из oop_checker.groovy.
// ─────────────────────────────────────────────────────────────────────────────

tasks {

    task("Task-1-1") {
        name            = "Hello World (Простой вывод)"
        maxScore        = 50
        softDeadline    = "2024-02-20"
        hardDeadline    = "2024-02-27"
    }

    task("Task-1-2") {
        name            = "Дроби (Fractions)"
        maxScore        = 100
        softDeadline    = "2024-03-05"
        hardDeadline    = "2024-03-12"
    }

    task("Task-2-1") {
        name            = "Стек и очередь"
        maxScore        = 100
        softDeadline    = "2024-03-19"
        hardDeadline    = "2024-03-26"
    }

    task("Task-2-2") {
        name            = "Граф (обходы BFS/DFS)"
        maxScore        = 150
        softDeadline    = "2024-04-09"
        hardDeadline    = "2024-04-16"
    }

    task("Task-2-3") {
        name            = "Калькулятор (паттерн Visitor)"
        maxScore        = 150
        softDeadline    = "2024-04-23"
        hardDeadline    = "2024-04-30"
    }

    task("Task-3-1") {
        name            = "Prime-числа (Stream API)"
        maxScore        = 100
        softDeadline    = "2024-05-14"
        hardDeadline    = "2024-05-21"
    }

    task("Task-3-2") {
        name            = "Записная книжка (JavaFX/Swing)"
        maxScore        = 200
        softDeadline    = "2024-05-28"
        hardDeadline    = "2024-06-04"
    }
}

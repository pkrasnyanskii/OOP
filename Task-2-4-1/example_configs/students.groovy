// ─────────────────────────────────────────────────────────────────────────────
// students.groovy  —  группы и студенты семестра
// Меняется раз в семестр при формировании новых групп.
// ─────────────────────────────────────────────────────────────────────────────

groups {

    group("22201") {
        student {
            github = "ivanov-ivan"
            name   = "Иванов Иван Иванович"
            repo   = "https://github.com/ivanov-ivan/OOP"
        }
        student {
            github = "petrov-petr"
            name   = "Петров Пётр Петрович"
            repo   = "https://github.com/petrov-petr/OOP"
        }
        student {
            github = "sidorova-anna"
            name   = "Сидорова Анна Владимировна"
            repo   = "https://github.com/sidorova-anna/OOP"
        }
    }

    group("22202") {
        student {
            github = "kozlov-dmitry"
            name   = "Козлов Дмитрий Александрович"
            repo   = "https://github.com/kozlov-dmitry/OOP"
        }
        student {
            github = "novikova-elena"
            name   = "Новикова Елена Сергеевна"
            repo   = "https://github.com/novikova-elena/OOP"
        }
    }
}

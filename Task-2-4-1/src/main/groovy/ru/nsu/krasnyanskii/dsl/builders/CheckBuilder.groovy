package ru.nsu.krasnyanskii.dsl.builders

import ru.nsu.krasnyanskii.model.CheckInstruction
import ru.nsu.krasnyanskii.model.OopCheckerConfig

/** Handles the {@code check { students ...; tasks ... }} DSL block. */
class CheckBuilder {
    private final OopCheckerConfig  config
    private final CheckInstruction  instruction

    CheckBuilder(OopCheckerConfig config) {
        this.config      = config
        this.instruction = config.getCheckInstruction()
    }

    /** Accepts GitHub logins as varargs: {@code students "login1", "login2"} */
    void students(String... githubs) {
        instruction.studentGithubs.addAll(githubs.toList())
    }

    void setStudents(List<String> githubs) {
        instruction.studentGithubs.addAll(githubs)
    }

    /** Accepts task ids as varargs: {@code tasks "Task-1-1", "Task-1-2"} */
    void tasks(String... taskIds) {
        instruction.taskIds.addAll(taskIds.toList())
    }

    void setTasks(List<String> taskIds) {
        instruction.taskIds.addAll(taskIds)
    }
}

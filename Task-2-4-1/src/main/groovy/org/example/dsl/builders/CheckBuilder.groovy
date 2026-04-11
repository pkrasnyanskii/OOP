package org.example.dsl.builders

import org.example.model.CheckInstruction
import org.example.model.OopCheckerConfig

class CheckBuilder {
    private final OopCheckerConfig config
    private final CheckInstruction instruction

    CheckBuilder(OopCheckerConfig config) {
        this.config = config
        this.instruction = config.getCheckInstruction()
    }

    /** Принимает список ников GitHub: students "github1", "github2" */
    void students(String... githubs) {
        instruction.studentGithubs.addAll(githubs.toList())
    }

    /** Принимает List<String> */
    void setStudents(List<String> githubs) {
        instruction.studentGithubs.addAll(githubs)
    }

    /** Принимает список идентификаторов задач: tasks "Task-1-1", "Task-1-2" */
    void tasks(String... taskIds) {
        instruction.taskIds.addAll(taskIds.toList())
    }

    void setTasks(List<String> taskIds) {
        instruction.taskIds.addAll(taskIds)
    }
}

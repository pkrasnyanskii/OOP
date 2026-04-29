package ru.nsu.krasnyanskii.dsl.builders

import ru.nsu.krasnyanskii.model.CheckPoint

import java.time.LocalDate

/** Builds a CheckPoint model object from DSL property assignments. */
class CheckPointBuilder {
    private final String name
    String       date
    List<String> tasks   = []
    boolean      isFinal = false

    CheckPointBuilder(String name) {
        this.name = name
    }

    CheckPoint build() {
        def cp = new CheckPoint(name, date ? LocalDate.parse(date) : null)
        cp.setTaskIds(tasks)
        cp.setFinal(isFinal)
        return cp
    }
}

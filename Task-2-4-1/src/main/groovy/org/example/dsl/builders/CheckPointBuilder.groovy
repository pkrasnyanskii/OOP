package org.example.dsl.builders

import org.example.model.CheckPoint

import java.time.LocalDate

class CheckPointBuilder {
    private final String name
    String date
    List<String> tasks = []
    boolean isFinal = false

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

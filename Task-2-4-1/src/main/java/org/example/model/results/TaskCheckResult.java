package org.example.model.results;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Результат проверки одной задачи для одного студента.
 *
 * Пайплайн (строго по ТЗ):
 *   1. compileStatus  — результат компиляции (FAILED → дальше не идём)
 *   2. docsStatus     — генерация javadoc
 *   2. styleStatus    — проверка Google Java Style
 *   3. testStatus     — запуск тестов (только если оба пункта 2 прошли)
 *
 * score            — итоговый балл (вычисляется ScoreCalculator)
 * lastCommitDate   — дата последнего коммита в директорию задачи (для дедлайнов)
 * *Output          — вывод соответствующего gradle-таска (для отладки)
 */
@Getter
@Setter
public class TaskCheckResult {
    private final String taskId;

    private BuildStatus compileStatus = BuildStatus.NOT_CHECKED;
    private BuildStatus docsStatus    = BuildStatus.NOT_CHECKED;
    private BuildStatus styleStatus   = BuildStatus.NOT_CHECKED;
    private BuildStatus testStatus    = BuildStatus.NOT_CHECKED;

    private TestCounts  testCounts      = new TestCounts();
    private LocalDate   lastCommitDate;
    private double      score           = 0.0;

    private String compileOutput = "";
    private String docsOutput    = "";
    private String styleOutput   = "";
    private String testOutput    = "";

    public TaskCheckResult(String taskId) {
        this.taskId = taskId;
    }
}

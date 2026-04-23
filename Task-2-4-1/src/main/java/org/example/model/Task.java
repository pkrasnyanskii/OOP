package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Описание учебной задачи (лабораторной работы).
 *
 * id          — уникальный идентификатор, совпадает с названием поддиректории в репо студента
 * name        — человекочитаемое название
 * maxScore    — максимальный балл за задачу
 * softDeadline — мягкий дедлайн: после него начисляется штраф за каждый день опоздания
 * hardDeadline — жёсткий дедлайн: после него балл = 0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    private String id;
    private String name;
    private double maxScore;
    private LocalDate softDeadline;
    private LocalDate hardDeadline;
}

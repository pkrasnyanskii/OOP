package org.example.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Задание на текущую проверку: каких студентов и какие задачи проверять.
 * Заполняется из блока check { ... } в конфиг-файле.
 */
@Data
public class CheckInstruction {
    private List<String> studentGithubs = new ArrayList<>();
    private List<String> taskIds = new ArrayList<>();
}

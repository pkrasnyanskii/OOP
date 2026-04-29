package ru.nsu.krasnyanskii.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/** Specifies which students and tasks to check in this run. */
@Data
public class CheckInstruction {
    private List<String> studentGithubs = new ArrayList<>();
    private List<String> taskIds        = new ArrayList<>();
}

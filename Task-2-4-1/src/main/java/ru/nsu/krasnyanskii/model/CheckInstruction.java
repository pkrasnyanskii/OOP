package ru.nsu.krasnyanskii.model;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/** Specifies which students and tasks to check in this run. */
@Data
public class CheckInstruction {
    private List<String> studentGithubs = new ArrayList<>();
    private List<String> taskIds        = new ArrayList<>();
}

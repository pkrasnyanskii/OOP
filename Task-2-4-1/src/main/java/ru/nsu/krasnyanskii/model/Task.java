package ru.nsu.krasnyanskii.model;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A course task (lab assignment).
 * The id must match the subdirectory name in the student's repository.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    private String    id;
    private String    name;
    private double    maxScore;
    private LocalDate softDeadline;
    private LocalDate hardDeadline;
}

package ru.nsu.krasnyanskii.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** A course student identified by their GitHub login. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    private String github;
    private String fullName;
    private String repoUrl;
}

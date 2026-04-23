package ru.nsu.krasnyanskii.model;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/** A course group containing a list of students. */
@Getter
@Setter
public class Group {
    private String        name;
    private List<Student> students = new ArrayList<>();

    public Group() {
    }

    public Group(String name) {
        this.name = name;
    }

    /** Adds a student to this group. */
    public void addStudent(Student student) {
        this.students.add(student);
    }
}

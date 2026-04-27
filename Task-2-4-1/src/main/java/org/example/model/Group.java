package org.example.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Учебная группа, содержащая список студентов.
 * Используем @Getter/@Setter вместо @Data, т.к. нужен кастомный конструктор с именем.
 */
@Getter
@Setter
public class Group {
    private String name;
    private List<Student> students = new ArrayList<>();

    public Group() {}

    public Group(String name) {
        this.name = name;
    }

    public void addStudent(Student student) {
        this.students.add(student);
    }
}

package org.example.model;

import java.util.ArrayList;
import java.util.List;

public class CheckInstruction {
    private List<String> studentGithubs = new ArrayList<>();
    private List<String> taskIds = new ArrayList<>();

    public List<String> getStudentGithubs() { return studentGithubs; }
    public void setStudentGithubs(List<String> studentGithubs) { this.studentGithubs = studentGithubs; }

    public List<String> getTaskIds() { return taskIds; }
    public void setTaskIds(List<String> taskIds) { this.taskIds = taskIds; }
}

package org.example.model;

public class Student {
    private String github;
    private String fullName;
    private String repoUrl;

    public Student() {}

    public Student(String github, String fullName, String repoUrl) {
        this.github = github;
        this.fullName = fullName;
        this.repoUrl = repoUrl;
    }

    public String getGithub() { return github; }
    public void setGithub(String github) { this.github = github; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getRepoUrl() { return repoUrl; }
    public void setRepoUrl(String repoUrl) { this.repoUrl = repoUrl; }

    @Override
    public String toString() {
        return "Student{github='" + github + "', fullName='" + fullName + "'}";
    }
}

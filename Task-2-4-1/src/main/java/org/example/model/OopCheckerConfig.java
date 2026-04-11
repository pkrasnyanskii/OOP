package org.example.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OopCheckerConfig {
    private List<Task> tasks = new ArrayList<>();
    private List<Group> groups = new ArrayList<>();
    private CheckInstruction checkInstruction = new CheckInstruction();
    private List<CheckPoint> checkPoints = new ArrayList<>();
    private ScoringConfig scoringConfig = new ScoringConfig();
    private List<BonusEntry> bonusEntries = new ArrayList<>();
    private ActivityConfig activityConfig;

    public List<Task> getTasks() { return tasks; }
    public void setTasks(List<Task> tasks) { this.tasks = tasks; }
    public void addTask(Task task) { this.tasks.add(task); }

    public List<Group> getGroups() { return groups; }
    public void setGroups(List<Group> groups) { this.groups = groups; }
    public void addGroup(Group group) { this.groups.add(group); }

    public CheckInstruction getCheckInstruction() { return checkInstruction; }
    public void setCheckInstruction(CheckInstruction checkInstruction) {
        this.checkInstruction = checkInstruction;
    }

    public List<CheckPoint> getCheckPoints() { return checkPoints; }
    public void setCheckPoints(List<CheckPoint> checkPoints) { this.checkPoints = checkPoints; }
    public void addCheckPoint(CheckPoint cp) { this.checkPoints.add(cp); }

    public ScoringConfig getScoringConfig() { return scoringConfig; }
    public void setScoringConfig(ScoringConfig scoringConfig) { this.scoringConfig = scoringConfig; }

    public List<BonusEntry> getBonusEntries() { return bonusEntries; }
    public void setBonusEntries(List<BonusEntry> bonusEntries) { this.bonusEntries = bonusEntries; }
    public void addBonusEntry(BonusEntry entry) { this.bonusEntries.add(entry); }

    public ActivityConfig getActivityConfig() { return activityConfig; }
    public void setActivityConfig(ActivityConfig activityConfig) { this.activityConfig = activityConfig; }

    public Optional<Task> findTaskById(String id) {
        return tasks.stream().filter(t -> t.getId().equals(id)).findFirst();
    }

    public Optional<Student> findStudentByGithub(String github) {
        return groups.stream()
                .flatMap(g -> g.getStudents().stream())
                .filter(s -> s.getGithub().equals(github))
                .findFirst();
    }

    public Optional<Group> findGroupByStudentGithub(String github) {
        return groups.stream()
                .filter(g -> g.getStudents().stream().anyMatch(s -> s.getGithub().equals(github)))
                .findFirst();
    }

    public double getBonusFor(String github, String taskId) {
        return bonusEntries.stream()
                .filter(e -> e.getStudentGithub().equals(github) && e.getTaskId().equals(taskId))
                .mapToDouble(BonusEntry::getPoints)
                .sum();
    }
}

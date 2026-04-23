package ru.nsu.krasnyanskii.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Root configuration object populated from DSL files.
 * Uses explicit getters/setters instead of {@code @Data} because
 * business-logic methods (find*, getBonusFor) would clash with generated equals/hashCode.
 */
@Getter
@Setter
public class OopCheckerConfig {
    private List<Task>       tasks            = new ArrayList<>();
    private List<Group>      groups           = new ArrayList<>();
    private CheckInstruction checkInstruction = new CheckInstruction();
    private List<CheckPoint> checkPoints      = new ArrayList<>();
    private ScoringConfig    scoringConfig    = new ScoringConfig();
    private List<BonusEntry> bonusEntries     = new ArrayList<>();
    private ActivityConfig   activityConfig;

    /** Appends a task definition. */
    public void addTask(Task task)           { tasks.add(task); }
    /** Appends a student group. */
    public void addGroup(Group group)        { groups.add(group); }
    /** Appends a grading checkpoint. */
    public void addCheckPoint(CheckPoint cp) { checkPoints.add(cp); }
    /** Appends a bonus entry. */
    public void addBonusEntry(BonusEntry e)  { bonusEntries.add(e); }

    /**
     * Finds a task by its id.
     *
     * @param id task identifier
     * @return Optional containing the task, or empty
     */
    public Optional<Task> findTaskById(String id) {
        return tasks.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst();
    }

    /**
     * Finds a student by their GitHub login across all groups.
     *
     * @param github GitHub username
     * @return Optional containing the student, or empty
     */
    public Optional<Student> findStudentByGithub(String github) {
        return groups.stream()
                .flatMap(g -> g.getStudents().stream())
                .filter(s -> s.getGithub().equals(github))
                .findFirst();
    }

    /**
     * Finds the group that contains the student with the given GitHub login.
     *
     * @param github GitHub username
     * @return Optional containing the group, or empty
     */
    public Optional<Group> findGroupByStudentGithub(String github) {
        return groups.stream()
                .filter(g -> g.getStudents().stream()
                              .anyMatch(s -> s.getGithub().equals(github)))
                .findFirst();
    }

    /**
     * Returns the total bonus points for a student on a specific task.
     *
     * @param github GitHub username
     * @param taskId task identifier
     * @return sum of all matching bonus entries, or 0.0
     */
    public double getBonusFor(String github, String taskId) {
        return bonusEntries.stream()
                .filter(e -> e.getStudentGithub().equals(github)
                          && e.getTaskId().equals(taskId))
                .mapToDouble(BonusEntry::getPoints)
                .sum();
    }
}

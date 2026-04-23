package org.example.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Корневой объект конфигурации — хранит всё, что было прочитано из DSL-файлов.
 *
 * Используем @Getter/@Setter вместо @Data, потому что:
 *   1. Нужны бизнес-методы (findTaskById, getBonusFor и т.д.)
 *   2. equals/hashCode по всем полям не имеет смысла для такого «God object»
 */
@Getter
@Setter
public class OopCheckerConfig {
    private List<Task>          tasks            = new ArrayList<>();
    private List<Group>         groups           = new ArrayList<>();
    private CheckInstruction    checkInstruction = new CheckInstruction();
    private List<CheckPoint>    checkPoints      = new ArrayList<>();
    private ScoringConfig       scoringConfig    = new ScoringConfig();
    private List<BonusEntry>    bonusEntries     = new ArrayList<>();
    private ActivityConfig      activityConfig;

    // ── Методы добавления элементов ───────────────────────────────────────

    public void addTask(Task task)          { tasks.add(task); }
    public void addGroup(Group group)       { groups.add(group); }
    public void addCheckPoint(CheckPoint cp){ checkPoints.add(cp); }
    public void addBonusEntry(BonusEntry e) { bonusEntries.add(e); }

    // ── Поисковые методы (используются в ScoreCalculator и HtmlReporter) ─

    public Optional<Task> findTaskById(String id) {
        return tasks.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst();
    }

    public Optional<Student> findStudentByGithub(String github) {
        return groups.stream()
                .flatMap(g -> g.getStudents().stream())
                .filter(s -> s.getGithub().equals(github))
                .findFirst();
    }

    public Optional<Group> findGroupByStudentGithub(String github) {
        return groups.stream()
                .filter(g -> g.getStudents().stream()
                              .anyMatch(s -> s.getGithub().equals(github)))
                .findFirst();
    }

    /** Суммирует все бонусные баллы данного студента за данную задачу. */
    public double getBonusFor(String github, String taskId) {
        return bonusEntries.stream()
                .filter(e -> e.getStudentGithub().equals(github)
                          && e.getTaskId().equals(taskId))
                .mapToDouble(BonusEntry::getPoints)
                .sum();
    }
}

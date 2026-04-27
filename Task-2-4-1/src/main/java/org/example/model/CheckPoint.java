package org.example.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Контрольная точка курса.
 *
 * name    — название (например "КТ-1")
 * date    — дата контрольной точки
 * taskIds — список id задач, которые учитываются в этой точке
 * isFinal — признак итоговой аттестации
 */
@Getter
@Setter
@NoArgsConstructor
public class CheckPoint {
    private String name;
    private LocalDate date;
    private List<String> taskIds = new ArrayList<>();
    private boolean isFinal = false;

    public CheckPoint(String name, LocalDate date) {
        this.name = name;
        this.date = date;
    }

    public void addTaskId(String taskId) {
        this.taskIds.add(taskId);
    }
}

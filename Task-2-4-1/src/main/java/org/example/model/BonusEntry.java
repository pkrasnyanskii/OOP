package org.example.model;

import lombok.Value;

/**
 * Запись о дополнительных баллах для конкретного студента за конкретную задачу.
 * @Value делает класс иммутабельным: все поля final, только геттеры, нет сеттеров.
 */
@Value
public class BonusEntry {
    String studentGithub;
    String taskId;
    double points;
}

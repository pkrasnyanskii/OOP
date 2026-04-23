package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Студент курса.
 *
 * github  — ник на GitHub (используется как ключ везде в системе)
 * fullName — ФИО для отображения в отчёте
 * repoUrl — ссылка на репозиторий (https или ssh)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    private String github;
    private String fullName;
    private String repoUrl;
}

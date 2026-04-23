package ru.nsu.krasnyanskii.report;

import ru.nsu.krasnyanskii.model.ActivityConfig;
import ru.nsu.krasnyanskii.model.CheckPoint;
import ru.nsu.krasnyanskii.model.GradeScale;
import ru.nsu.krasnyanskii.model.OopCheckerConfig;
import ru.nsu.krasnyanskii.model.Task;
import ru.nsu.krasnyanskii.model.results.BuildStatus;
import ru.nsu.krasnyanskii.model.results.StudentCheckResult;
import ru.nsu.krasnyanskii.model.results.TaskCheckResult;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/** Generates a self-contained HTML report from check results. */
public class HtmlReporter {
    private final OopCheckerConfig config;

    public HtmlReporter(OopCheckerConfig config) {
        this.config = config;
    }

    /**
     * Generates the HTML report string.
     *
     * @param results per-student check results
     * @return full HTML document as a string
     */
    public String generate(List<StudentCheckResult> results) {
        List<String> checkedTaskIds = config.getCheckInstruction().getTaskIds();
        StringBuilder sb = new StringBuilder();

        sb.append("<!DOCTYPE html>\n<html lang=\"ru\">\n<head>\n")
          .append("<meta charset=\"UTF-8\">\n")
          .append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n")
          .append("<title>OOP Check Report</title>\n")
          .append(CSS)
          .append("</head>\n<body>\n");

        sb.append("<h1>Отчёт автоматической проверки задач по ООП</h1>\n");
        sb.append("<p class=\"meta\">Сгенерировано: ")
          .append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")))
          .append("</p>\n");

        // Task results table
        sb.append("<h2>Результаты проверки задач</h2>\n");
        sb.append("<div class=\"scroll\">\n<table>\n<thead>\n<tr>\n");
        sb.append("<th>Студент</th><th>Группа</th>");
        for (String tid : checkedTaskIds) {
            Task t = config.findTaskById(tid).orElse(null);
            String label = t != null
                    ? tid + "<br><small>" + t.getName() + "</small><br><small>(" + (int) t.getMaxScore() + " б)</small>"
                    : tid;
            sb.append("<th>").append(label).append("</th>");
        }
        sb.append("<th>Сумма</th></tr>\n</thead>\n<tbody>\n");

        for (StudentCheckResult sr : results) {
            sb.append("<tr>\n");
            sb.append("<td><b>").append(esc(sr.getStudentName())).append("</b><br>")
              .append("<small>@").append(esc(sr.getStudentGithub())).append("</small></td>\n");
            sb.append("<td>").append(esc(sr.getGroupName())).append("</td>\n");

            double totalScore = 0;
            for (String tid : checkedTaskIds) {
                Optional<TaskCheckResult> trOpt = sr.getTaskResult(tid);
                if (trOpt.isEmpty()) {
                    sb.append("<td class=\"na\">—</td>\n");
                    continue;
                }
                TaskCheckResult tr = trOpt.get();
                totalScore += tr.getScore();
                sb.append(renderTaskCell(tr));
            }

            totalScore += sr.getActivityBonus();
            sb.append("<td class=\"total\"><b>").append(fmt(totalScore)).append("</b>");
            if (sr.getActivityBonus() > 0) {
                sb.append("<br><small>+").append(fmt(sr.getActivityBonus())).append(" (активность)</small>");
            }
            sb.append("</td>\n</tr>\n");
        }
        sb.append("</tbody>\n</table>\n</div>\n");

        // Checkpoint scores
        if (!config.getCheckPoints().isEmpty()) {
            sb.append("<h2>Контрольные точки</h2>\n");
            sb.append("<div class=\"scroll\">\n<table>\n<thead>\n<tr>\n");
            sb.append("<th>Студент</th><th>Группа</th>");
            for (CheckPoint cp : config.getCheckPoints()) {
                sb.append("<th>").append(esc(cp.getName())).append("<br><small>")
                  .append(cp.getDate()).append("</small></th>");
            }
            sb.append("</tr>\n</thead>\n<tbody>\n");

            for (StudentCheckResult sr : results) {
                sb.append("<tr>\n");
                sb.append("<td><b>").append(esc(sr.getStudentName())).append("</b></td>\n");
                sb.append("<td>").append(esc(sr.getGroupName())).append("</td>\n");
                for (CheckPoint cp : config.getCheckPoints()) {
                    double cpScore = cp.getTaskIds().stream()
                            .mapToDouble(tid -> sr.getTaskResult(tid)
                                    .map(TaskCheckResult::getScore).orElse(0.0))
                            .sum();
                    double cpMax = cp.getTaskIds().stream()
                            .mapToDouble(tid -> config.findTaskById(tid)
                                    .map(Task::getMaxScore).orElse(0.0))
                            .sum();
                    int grade = gradeForScore(cpScore, cpMax > 0 ? cpMax : 100);
                    String cls = gradeClass(grade);
                    sb.append("<td class=\"").append(cls).append("\">")
                      .append(fmt(cpScore)).append(" / ").append(fmt(cpMax))
                      .append("<br><b>Оценка: ").append(grade).append("</b></td>\n");
                }
                sb.append("</tr>\n");
            }
            sb.append("</tbody>\n</table>\n</div>\n");
        }

        // Activity bonus table
        if (config.getActivityConfig() != null) {
            ActivityConfig ac = config.getActivityConfig();
            sb.append("<h2>Активность студентов</h2>\n");
            sb.append("<p>Период: ").append(ac.getCourseStart()).append(" — ").append(ac.getCourseEnd())
              .append(". Порог активных недель: <b>").append(ac.getMinActiveWeeks())
              .append("</b>. Бонус: <b>").append(fmt(ac.getBonusPoints())).append(" б.</b></p>\n");
            sb.append("<div class=\"scroll\">\n<table>\n<thead>\n<tr>")
              .append("<th>Студент</th><th>Группа</th><th>Активных недель</th><th>Бонус</th></tr>\n</thead>\n<tbody>\n");
            for (StudentCheckResult sr : results) {
                String cls = sr.getActiveWeeks() >= ac.getMinActiveWeeks() ? "pass" : "warn";
                sb.append("<tr>")
                  .append("<td><b>").append(esc(sr.getStudentName())).append("</b></td>")
                  .append("<td>").append(esc(sr.getGroupName())).append("</td>")
                  .append("<td class=\"").append(cls).append("\">").append(sr.getActiveWeeks()).append("</td>")
                  .append("<td>").append(fmt(sr.getActivityBonus())).append(" б.</td>")
                  .append("</tr>\n");
            }
            sb.append("</tbody>\n</table>\n</div>\n");
        }

        // Final grades
        sb.append("<h2>Итоговые оценки</h2>\n");
        double totalMaxScore = checkedTaskIds.stream()
                .mapToDouble(tid -> config.findTaskById(tid).map(Task::getMaxScore).orElse(0.0))
                .sum();
        if (config.getActivityConfig() != null) {
            totalMaxScore += config.getActivityConfig().getBonusPoints();
        }
        sb.append("<div class=\"scroll\">\n<table>\n<thead>\n<tr>")
          .append("<th>Студент</th><th>Группа</th>")
          .append("<th>Сумма баллов</th><th>% от макс.</th><th>Оценка</th></tr>\n</thead>\n<tbody>\n");
        for (StudentCheckResult sr : results) {
            double score = sr.getTotalScore();
            double pct   = totalMaxScore > 0 ? score / totalMaxScore * 100 : 0;
            int grade    = gradeForScore(score, totalMaxScore > 0 ? totalMaxScore : 100);
            String cls   = gradeClass(grade);
            sb.append("<tr>")
              .append("<td><b>").append(esc(sr.getStudentName())).append("</b></td>")
              .append("<td>").append(esc(sr.getGroupName())).append("</td>")
              .append("<td>").append(fmt(score)).append(" / ").append(fmt(totalMaxScore)).append("</td>")
              .append("<td>").append(String.format("%.1f%%", pct)).append("</td>")
              .append("<td class=\"grade ").append(cls).append("\"><b>").append(grade).append("</b></td>")
              .append("</tr>\n");
        }
        sb.append("</tbody>\n</table>\n</div>\n");

        // Legend
        sb.append("<h2>Легенда</h2>\n");
        GradeScale gs = config.getScoringConfig().getGradeScale();
        sb.append("<p>Оценки: ")
          .append("5 ≥ ").append(fmt(gs.getExcellent())).append("%, ")
          .append("4 ≥ ").append(fmt(gs.getGood())).append("%, ")
          .append("3 ≥ ").append(fmt(gs.getSatisfactory())).append("%, ")
          .append("иначе 2</p>\n");
        sb.append("<p><span class=\"pass\">■</span> OK &nbsp; ")
          .append("<span class=\"fail\">■</span> Ошибка &nbsp; ")
          .append("<span class=\"warn\">■</span> Частично &nbsp; ")
          .append("<span class=\"na\">■</span> Н/Д</p>\n");

        sb.append("</body>\n</html>\n");
        return sb.toString();
    }

    private String renderTaskCell(TaskCheckResult tr) {
        if (tr.getCompileStatus() == BuildStatus.FAILED) {
            return "<td class=\"fail\">Ошибка компиляции</td>\n";
        }
        if (tr.getCompileStatus() == BuildStatus.TIMEOUT) {
            return "<td class=\"fail\">Таймаут компиляции</td>\n";
        }
        if (tr.getCompileStatus() == BuildStatus.NOT_CHECKED) {
            return "<td class=\"na\">Не проверялось</td>\n";
        }

        StringBuilder cell = new StringBuilder();
        double score = tr.getScore();
        String scoreClass = score > 0 ? (tr.getTestCounts().getFailed() > 0 ? "warn" : "pass") : "fail";

        cell.append("<td class=\"").append(scoreClass).append("\">");
        cell.append("<b>").append(fmt(score)).append(" б.</b><br>");
        cell.append("<small>").append(tr.getTestCounts()).append("</small><br>");
        cell.append("<small>Style: ").append(statusBadge(tr.getStyleStatus())).append("</small><br>");
        cell.append("<small>Docs: ").append(statusBadge(tr.getDocsStatus())).append("</small>");

        if (tr.getLastCommitDate() != null) {
            cell.append("<br><small>Коммит: ").append(tr.getLastCommitDate()).append("</small>");
        }

        cell.append("</td>\n");
        return cell.toString();
    }

    private String statusBadge(BuildStatus status) {
        return switch (status) {
            case SUCCESS       -> "<span class=\"pass\">✓</span>";
            case FAILED        -> "<span class=\"fail\">✗</span>";
            case TIMEOUT       -> "<span class=\"fail\">⏱</span>";
            case NOT_AVAILABLE -> "<span class=\"na\">—</span>";
            case NOT_CHECKED   -> "<span class=\"na\">?</span>";
        };
    }

    private int gradeForScore(double score, double maxScore) {
        double pct = maxScore > 0 ? score / maxScore * 100 : 0;
        return config.getScoringConfig().getGradeScale().toGrade(pct);
    }

    private String gradeClass(int grade) {
        return switch (grade) {
            case 5, 4 -> "pass";
            case 3    -> "warn";
            default   -> "fail";
        };
    }

    private String fmt(double v) {
        if (v == Math.floor(v)) return String.valueOf((int) v);
        return String.format("%.1f", v);
    }

    private String esc(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private static final String CSS = """
            <style>
              body { font-family: Arial, sans-serif; margin: 24px; background: #f5f5f5; color: #222; }
              h1 { color: #2c3e50; border-bottom: 3px solid #3498db; padding-bottom: 8px; }
              h2 { color: #34495e; margin-top: 32px; }
              .meta { color: #7f8c8d; font-size: 0.9em; }
              .scroll { overflow-x: auto; }
              table { border-collapse: collapse; min-width: 600px; background: #fff;
                      box-shadow: 0 1px 4px rgba(0,0,0,.1); }
              th { background: #2c3e50; color: #fff; padding: 10px 14px; text-align: left; font-size: 0.85em; }
              td { padding: 8px 14px; border-bottom: 1px solid #e0e0e0; font-size: 0.85em; vertical-align: top; }
              tr:hover td { background: #f0f7ff; }
              .pass { background-color: #d4edda; color: #155724; }
              .fail { background-color: #f8d7da; color: #721c24; }
              .warn { background-color: #fff3cd; color: #856404; }
              .na   { color: #aaa; }
              .total { font-size: 1em; background-color: #e8f4fd; }
              .grade { font-size: 1.1em; text-align: center; }
              small { font-size: 0.8em; }
            </style>
            """;
}

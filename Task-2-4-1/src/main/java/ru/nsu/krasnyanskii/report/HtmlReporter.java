package ru.nsu.krasnyanskii.report;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import ru.nsu.krasnyanskii.checker.GradeCalculator;
import ru.nsu.krasnyanskii.model.ActivityConfig;
import ru.nsu.krasnyanskii.model.CheckPoint;
import ru.nsu.krasnyanskii.model.GradeScale;
import ru.nsu.krasnyanskii.model.OopCheckerConfig;
import ru.nsu.krasnyanskii.model.Task;
import ru.nsu.krasnyanskii.model.results.BuildStatus;
import ru.nsu.krasnyanskii.model.results.StudentCheckResult;
import ru.nsu.krasnyanskii.model.results.TaskCheckResult;

/** Generates a self-contained HTML report from check results. */
public class HtmlReporter {

    private final OopCheckerConfig config;
    private final GradeCalculator  gradeCalc;

    /**
     * Creates an HtmlReporter.
     *
     * @param config parsed OOP checker configuration
     */
    public HtmlReporter(OopCheckerConfig config) {
        this.config    = config;
        this.gradeCalc = new GradeCalculator(config);
    }

    /**
     * Generates the HTML report string.
     *
     * @param results per-student check results
     * @return full HTML document as a string
     */
    public String generate(List<StudentCheckResult> results) {
        final List<String> checkedTaskIds = config.getCheckInstruction().getTaskIds();
        StringBuilder sb = new StringBuilder();

        sb.append("<!DOCTYPE html>\n<html lang=\"ru\">\n<head>\n");
        sb.append("<meta charset=\"UTF-8\">\n");
        sb.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        sb.append("<title>OOP Check Report</title>\n");
        sb.append(CSS);
        sb.append("</head>\n<body>\n");

        sb.append("<h1>Отчёт автоматической проверки задач по ООП</h1>\n");
        sb.append("<p class=\"meta\">Сгенерировано: ");
        sb.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));
        sb.append("</p>\n");

        appendTaskTable(sb, results, checkedTaskIds);

        if (!config.getCheckPoints().isEmpty()) {
            appendCheckpointTable(sb, results);
        }

        if (config.getActivityConfig() != null) {
            appendActivityTable(sb, results);
        }

        appendFinalGrades(sb, results, checkedTaskIds);
        appendLegend(sb);

        sb.append("</body>\n</html>\n");
        return sb.toString();
    }

    private void appendTaskTable(
            StringBuilder sb,
            List<StudentCheckResult> results,
            List<String> checkedTaskIds) {
        sb.append("<h2>Результаты проверки задач</h2>\n");
        sb.append("<div class=\"scroll\">\n<table>\n<thead>\n<tr>\n");
        sb.append("<th>Студент</th><th>Группа</th>");
        for (String tid : checkedTaskIds) {
            Task t = config.findTaskById(tid).orElse(null);
            String label;
            if (t != null) {
                label = tid + "<br><small>" + t.getName()
                        + "</small><br><small>(" + (int) t.getMaxScore() + " б)</small>";
            } else {
                label = tid;
            }
            sb.append("<th>").append(label).append("</th>");
        }
        sb.append("<th>Сумма</th></tr>\n</thead>\n<tbody>\n");

        for (StudentCheckResult sr : results) {
            sb.append("<tr>\n");
            sb.append("<td><b>").append(esc(sr.getStudentName())).append("</b><br>");
            sb.append("<small>@").append(esc(sr.getStudentGithub())).append("</small></td>\n");
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
                sb.append("<br><small>+").append(fmt(sr.getActivityBonus()));
                sb.append(" (активность)</small>");
            }
            sb.append("</td>\n</tr>\n");
        }
        sb.append("</tbody>\n</table>\n</div>\n");
    }

    private void appendCheckpointTable(StringBuilder sb, List<StudentCheckResult> results) {
        sb.append("<h2>Контрольные точки</h2>\n");
        sb.append("<div class=\"scroll\">\n<table>\n<thead>\n<tr>\n");
        sb.append("<th>Студент</th><th>Группа</th>");
        for (CheckPoint cp : config.getCheckPoints()) {
            sb.append("<th>").append(esc(cp.getName())).append("<br><small>");
            sb.append(cp.getDate()).append("</small></th>");
        }
        sb.append("</tr>\n</thead>\n<tbody>\n");

        for (StudentCheckResult sr : results) {
            sb.append("<tr>\n");
            sb.append("<td><b>").append(esc(sr.getStudentName())).append("</b></td>\n");
            sb.append("<td>").append(esc(sr.getGroupName())).append("</td>\n");
            for (CheckPoint cp : config.getCheckPoints()) {
                double cpScore = gradeCalc.checkpointScore(sr, cp);
                double cpMax   = gradeCalc.checkpointMax(cp);
                int grade      = gradeCalc.grade(cpScore, cpMax);
                String cls     = gradeCalc.gradeClass(grade);
                sb.append("<td class=\"").append(cls).append("\">");
                sb.append(fmt(cpScore)).append(" / ").append(fmt(cpMax));
                sb.append("<br><b>Оценка: ").append(grade).append("</b></td>\n");
            }
            sb.append("</tr>\n");
        }
        sb.append("</tbody>\n</table>\n</div>\n");
    }

    private void appendActivityTable(StringBuilder sb, List<StudentCheckResult> results) {
        ActivityConfig ac = config.getActivityConfig();
        sb.append("<h2>Активность студентов</h2>\n");
        sb.append("<p>Период: ").append(ac.getCourseStart());
        sb.append(" — ").append(ac.getCourseEnd());
        sb.append(". Порог активных недель: <b>").append(ac.getMinActiveWeeks());
        sb.append("</b>. Бонус: <b>").append(fmt(ac.getBonusPoints())).append(" б.</b></p>\n");
        sb.append("<div class=\"scroll\">\n<table>\n<thead>\n<tr>");
        sb.append("<th>Студент</th><th>Группа</th>");
        sb.append("<th>Активных недель</th><th>Бонус</th></tr>\n</thead>\n<tbody>\n");
        for (StudentCheckResult sr : results) {
            sb.append("<tr>");
            sb.append("<td><b>").append(esc(sr.getStudentName())).append("</b></td>");
            sb.append("<td>").append(esc(sr.getGroupName())).append("</td>");
            String cls = sr.getActiveWeeks() >= ac.getMinActiveWeeks() ? "pass" : "warn";
            sb.append("<td class=\"").append(cls).append("\">");
            sb.append(sr.getActiveWeeks()).append("</td>");
            sb.append("<td>").append(fmt(sr.getActivityBonus())).append(" б.</td>");
            sb.append("</tr>\n");
        }
        sb.append("</tbody>\n</table>\n</div>\n");
    }

    private void appendFinalGrades(
            StringBuilder sb,
            List<StudentCheckResult> results,
            List<String> checkedTaskIds) {
        sb.append("<h2>Итоговые оценки</h2>\n");
        double totalMaxScore = gradeCalc.totalMaxScore(checkedTaskIds);
        sb.append("<div class=\"scroll\">\n<table>\n<thead>\n<tr>");
        sb.append("<th>Студент</th><th>Группа</th>");
        sb.append("<th>Сумма баллов</th><th>% от макс.</th><th>Оценка</th>");
        sb.append("</tr>\n</thead>\n<tbody>\n");
        for (StudentCheckResult sr : results) {
            double score = sr.getTotalScore();
            int grade    = gradeCalc.grade(score, totalMaxScore);
            String cls   = gradeCalc.gradeClass(grade);
            sb.append("<tr>");
            sb.append("<td><b>").append(esc(sr.getStudentName())).append("</b></td>");
            sb.append("<td>").append(esc(sr.getGroupName())).append("</td>");
            sb.append("<td>").append(fmt(score)).append(" / ").append(fmt(totalMaxScore));
            sb.append("</td>");
            double pct = gradeCalc.scorePercent(score, totalMaxScore);
            sb.append("<td>").append(String.format("%.1f%%", pct)).append("</td>");
            sb.append("<td class=\"grade ").append(cls).append("\"><b>");
            sb.append(grade).append("</b></td>");
            sb.append("</tr>\n");
        }
        sb.append("</tbody>\n</table>\n</div>\n");
    }

    private void appendLegend(StringBuilder sb) {
        sb.append("<h2>Легенда</h2>\n");
        GradeScale gs = config.getScoringConfig().getGradeScale();
        sb.append("<p>Оценки: ");
        sb.append("5 ≥ ").append(fmt(gs.getExcellent())).append("%, ");
        sb.append("4 ≥ ").append(fmt(gs.getGood())).append("%, ");
        sb.append("3 ≥ ").append(fmt(gs.getSatisfactory())).append("%, ");
        sb.append("иначе 2</p>\n");
        sb.append("<p><span class=\"pass\">■</span> OK &nbsp; ");
        sb.append("<span class=\"fail\">■</span> Ошибка &nbsp; ");
        sb.append("<span class=\"warn\">■</span> Частично &nbsp; ");
        sb.append("<span class=\"na\">■</span> Н/Д</p>\n");
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
        boolean hasFailed = tr.getTestCounts().getFailed() > 0;
        String scoreClass = score > 0 ? (hasFailed ? "warn" : "pass") : "fail";

        cell.append("<td class=\"").append(scoreClass).append("\">");
        cell.append("<b>").append(fmt(score)).append(" б.</b><br>");
        cell.append("<small>").append(tr.getTestCounts()).append("</small><br>");
        cell.append("<small>Style: ")
                .append(statusBadge(tr.getStyleStatus())).append("</small><br>");
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
            case NOT_AVAILABLE -> "<span class=\"pass\">✓</span>";
            case NOT_CHECKED   -> "<span class=\"na\">?</span>";
        };
    }

    private String fmt(double v) {
        if (v == Math.floor(v)) {
            return String.valueOf((int) v);
        }
        return String.format("%.1f", v);
    }

    private String esc(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private static final String CSS = """
            <style>
              body { font-family: Arial, sans-serif; margin: 24px;
                     background: #f5f5f5; color: #222; }
              h1 { color: #2c3e50; border-bottom: 3px solid #3498db; padding-bottom: 8px; }
              h2 { color: #34495e; margin-top: 32px; }
              .meta { color: #7f8c8d; font-size: 0.9em; }
              .scroll { overflow-x: auto; }
              table { border-collapse: collapse; min-width: 600px; background: #fff;
                      box-shadow: 0 1px 4px rgba(0,0,0,.1); }
              th { background: #2c3e50; color: #fff; padding: 10px 14px; font-size: 0.85em; }
              td { padding: 8px 14px; border-bottom: 1px solid #e0e0e0; font-size: 0.85em; }
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

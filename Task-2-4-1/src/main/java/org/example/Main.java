package org.example;

import org.example.checker.GitManager;
import org.example.checker.ProcessResult;
import org.example.checker.ProcessRunner;
import org.example.checker.ProjectChecker;
import org.example.config.ConfigLoader;
import org.example.model.OopCheckerConfig;
import org.example.model.results.StudentCheckResult;
import org.example.report.HtmlReporter;

import java.io.File;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

/**
 * Точка входа.
 *
 * Работает как Gradle: ищет oop_checker.groovy в рабочей директории,
 * читает конфигурацию, запускает проверку, выводит HTML-отчёт в stdout.
 *
 * Перед запуском проверяет:
 *   1. git доступен в PATH
 *   2. git config --global user.name задан
 *   3. git настроен без запросов аутентификации (credential helper / SSH)
 *
 * Использование:
 *   java -jar oop-checker.jar [путь-к-конфигу] [--output report.html] [--skip-auth-check]
 */
public class Main {
    private static final Logger log = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) throws Exception {
        File   workDir         = new File(System.getProperty("user.dir"));
        String outputFile      = null;
        boolean skipAuthCheck  = false;

        for (int i = 0; i < args.length; i++) {
            if ("--output".equals(args[i]) && i + 1 < args.length) {
                outputFile = args[++i];
            } else if ("--skip-auth-check".equals(args[i])) {
                skipAuthCheck = true;
            } else if (!args[i].startsWith("--")) {
                workDir = new File(args[i]);
            }
        }

        System.err.println("=== OOP Checker ===");
        System.err.println("Рабочая директория: " + workDir.getAbsolutePath());

        // ── Шаг 0: Проверка окружения ─────────────────────────────────────────
        checkGitAvailable();
        checkGitUserConfigured();
        if (!skipAuthCheck) {
            warnIfAuthMayPrompt();
        }

        // ── Шаг 1: Загрузка конфигурации DSL ─────────────────────────────────
        OopCheckerConfig config;
        try {
            config = ConfigLoader.loadFromDirectory(workDir);
        } catch (Exception e) {
            // Печатаем полную цепочку причин, чтобы ошибку было легко диагностировать
            System.err.println();
            System.err.println("═══ ОШИБКА ЗАГРУЗКИ КОНФИГУРАЦИИ ═══");
            System.err.println(e.getMessage());
            Throwable cause = e.getCause();
            while (cause != null) {
                System.err.println("  Причина: " + cause.getMessage());
                cause = cause.getCause();
            }
            System.err.println();
            System.err.println("Подсказка: запустите с аргументом — путь к папке с oop_checker.groovy");
            System.err.println("  ./gradlew run --args=\"example_configs\"");
            System.err.println("  java -jar oop-checker.jar /path/to/configs");
            System.exit(1);
            return;
        }

        System.err.println("Конфиг загружен: "
                + config.getCheckInstruction().getStudentGithubs().size() + " студент(ов), "
                + config.getCheckInstruction().getTaskIds().size() + " задач(и).");

        // ── Шаг 2: Проверка репозиториев ─────────────────────────────────────
        Path reposDir = workDir.toPath().resolve("repos");
        ProjectChecker checker = new ProjectChecker(config, reposDir);
        List<StudentCheckResult> results = checker.runChecks();

        // ── Шаг 3: Генерация HTML-отчёта ─────────────────────────────────────
        HtmlReporter reporter = new HtmlReporter(config);
        String html = reporter.generate(results);

        if (outputFile != null) {
            try (PrintStream out = new PrintStream(outputFile, StandardCharsets.UTF_8)) {
                out.print(html);
            }
            System.err.println("Отчёт сохранён: " + outputFile);
        } else {
            // UTF-8 для stdout (особенно важно на Windows)
            new PrintStream(System.out, true, StandardCharsets.UTF_8).print(html);
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Предстартовые проверки (требование ТЗ §git)
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Проверяет, что git доступен в PATH.
     * ТЗ: «Работа с репозиториями должна осуществляться через консольный git-клиент».
     */
    private static void checkGitAvailable() {
        try {
            ProcessResult r = new ProcessRunner(10).run(
                    new File(".").toPath(), "git", "--version");
            if (r.isSuccess()) {
                System.err.println("Git: " + r.getOutput().trim());
            } else {
                System.err.println("ПРЕДУПРЕЖДЕНИЕ: git не найден в PATH. "
                        + "Установите git и повторите запуск.");
            }
        } catch (Exception e) {
            System.err.println("ПРЕДУПРЕЖДЕНИЕ: не удалось проверить git: " + e.getMessage());
        }
    }

    /**
     * Проверяет, что задан git config --global user.name.
     * ТЗ: «Работа с git идёт от имени пользователя, указанного в git config global».
     */
    private static void checkGitUserConfigured() {
        try {
            ProcessResult r = new ProcessRunner(10).run(
                    new File(".").toPath(), "git", "config", "--global", "user.name");
            if (r.isSuccess() && !r.getOutput().trim().isEmpty()) {
                System.err.println("Git user: " + r.getOutput().trim());
            } else {
                System.err.println("ПРЕДУПРЕЖДЕНИЕ: git config --global user.name не задан. "
                        + "Выполните: git config --global user.name \"Ваше Имя\"");
            }
        } catch (Exception e) {
            System.err.println("ПРЕДУПРЕЖДЕНИЕ: не удалось прочитать git user.name: " + e.getMessage());
        }
    }

    /**
     * Предупреждает, если git может запросить пароль (нет credential helper и не SSH).
     *
     * ТЗ: «Допускается работать с git, настроенным на отсутствие запросов
     * аутентификации пользователя; в этом случае перед запуском необходимо
     * проверять, что это действительно так.»
     */
    private static void warnIfAuthMayPrompt() {
        try {
            ProcessResult r = new ProcessRunner(10).run(
                    new File(".").toPath(), "git", "config", "--global", "credential.helper");
            boolean hasHelper = r.isSuccess() && !r.getOutput().trim().isEmpty();

            if (!hasHelper) {
                System.err.println();
                System.err.println("⚠  ВНИМАНИЕ: git credential.helper не настроен.");
                System.err.println("   При клонировании приватных репозиториев git может");
                System.err.println("   запросить пароль, что приведёт к зависанию.");
                System.err.println("   Рекомендации:");
                System.err.println("     • Настройте SSH-ключи и используйте ssh:// ссылки");
                System.err.println("     • Или: git config --global credential.helper store");
                System.err.println("     • Или запустите с флагом --skip-auth-check");
                System.err.println();
            } else {
                System.err.println("Git credential.helper: " + r.getOutput().trim() + " (OK)");
            }
        } catch (Exception e) {
            System.err.println("ПРЕДУПРЕЖДЕНИЕ: не удалось проверить credential.helper: "
                    + e.getMessage());
        }
    }
}

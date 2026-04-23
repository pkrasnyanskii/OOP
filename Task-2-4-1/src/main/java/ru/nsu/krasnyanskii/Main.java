package ru.nsu.krasnyanskii;

import java.io.File;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;
import ru.nsu.krasnyanskii.checker.ProcessResult;
import ru.nsu.krasnyanskii.checker.ProcessRunner;
import ru.nsu.krasnyanskii.checker.ProjectChecker;
import ru.nsu.krasnyanskii.config.ConfigLoader;
import ru.nsu.krasnyanskii.model.OopCheckerConfig;
import ru.nsu.krasnyanskii.model.results.StudentCheckResult;
import ru.nsu.krasnyanskii.report.HtmlReporter;

/**
 * Entry point. Reads oop_checker.groovy from the working directory,
 * runs checks for each student, and writes an HTML report to stdout.
 *
 * <p>Usage: {@code java -jar oop-checker.jar [config-dir] [--output report.html]
 * [--skip-auth-check]}
 */
public class Main {
    private static final Logger log = Logger.getLogger(Main.class.getName());

    /**
     * Application entry point.
     *
     * @param args command-line arguments
     * @throws Exception on any unrecoverable error
     */
    public static void main(String[] args) throws Exception {
        File    workDir       = new File(System.getProperty("user.dir"));
        String  outputFile    = null;
        boolean skipAuthCheck = false;

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
        System.err.println("Working dir: " + workDir.getAbsolutePath());

        // Step 0: environment checks
        checkGitAvailable();
        checkGitUserConfigured();
        if (!skipAuthCheck) {
            warnIfAuthMayPrompt();
        }

        // Step 1: load DSL config
        OopCheckerConfig config;
        try {
            config = ConfigLoader.loadFromDirectory(workDir);
        } catch (Exception e) {
            System.err.println();
            System.err.println("=== CONFIG LOAD ERROR ===");
            System.err.println(e.getMessage());
            Throwable cause = e.getCause();
            while (cause != null) {
                System.err.println("  Caused by: " + cause.getMessage());
                cause = cause.getCause();
            }
            System.err.println();
            System.err.println("Hint: pass the directory containing oop_checker.groovy");
            System.err.println("  ./gradlew run --args=\"example_configs\"");
            System.err.println("  java -jar oop-checker.jar /path/to/configs");
            System.exit(1);
            return;
        }

        System.err.println("Config loaded: "
                + config.getCheckInstruction().getStudentGithubs().size() + " student(s), "
                + config.getCheckInstruction().getTaskIds().size() + " task(s).");

        // Step 2: run checks
        Path reposDir = workDir.toPath().resolve("repos");
        ProjectChecker checker = new ProjectChecker(config, reposDir);
        List<StudentCheckResult> results = checker.runChecks();

        // Step 3: generate HTML report
        HtmlReporter reporter = new HtmlReporter(config);
        String html = reporter.generate(results);

        if (outputFile != null) {
            try (PrintStream out = new PrintStream(outputFile, StandardCharsets.UTF_8)) {
                out.print(html);
            }
            System.err.println("Report saved: " + outputFile);
        } else {
            new PrintStream(System.out, true, StandardCharsets.UTF_8).print(html);
        }
    }

    /** Checks that git is available on PATH. */
    private static void checkGitAvailable() {
        try {
            ProcessResult r = new ProcessRunner(10).run(
                    new File(".").toPath(), "git", "--version");
            if (r.isSuccess()) {
                System.err.println("Git: " + r.getOutput().trim());
            } else {
                System.err.println("WARNING: git not found on PATH. Install git and retry.");
            }
        } catch (Exception e) {
            System.err.println("WARNING: could not check git: " + e.getMessage());
        }
    }

    /** Checks that git config --global user.name is set. */
    private static void checkGitUserConfigured() {
        try {
            ProcessResult r = new ProcessRunner(10).run(
                    new File(".").toPath(), "git", "config", "--global", "user.name");
            if (r.isSuccess() && !r.getOutput().trim().isEmpty()) {
                System.err.println("Git user: " + r.getOutput().trim());
            } else {
                System.err.println("WARNING: git config --global user.name is not set.");
            }
        } catch (Exception e) {
            System.err.println("WARNING: could not read git user.name: " + e.getMessage());
        }
    }

    /** Warns if git may prompt for credentials (no credential helper and no SSH). */
    private static void warnIfAuthMayPrompt() {
        try {
            ProcessResult r = new ProcessRunner(10).run(
                    new File(".").toPath(), "git", "config", "--global", "credential.helper");
            boolean hasHelper = r.isSuccess() && !r.getOutput().trim().isEmpty();

            if (!hasHelper) {
                System.err.println();
                System.err.println("WARNING: git credential.helper is not configured.");
                System.err.println("  Cloning private repos may hang waiting for a password.");
                System.err.println("  Use SSH keys, set credential.helper, or --skip-auth-check");
                System.err.println();
            } else {
                System.err.println("Git credential.helper: " + r.getOutput().trim() + " (OK)");
            }
        } catch (Exception e) {
            System.err.println("WARNING: could not check credential.helper: " + e.getMessage());
        }
    }
}

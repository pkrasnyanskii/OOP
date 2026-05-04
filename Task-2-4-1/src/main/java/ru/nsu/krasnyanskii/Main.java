package ru.nsu.krasnyanskii;

import java.io.File;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import ru.nsu.krasnyanskii.checker.CheckerView;
import ru.nsu.krasnyanskii.checker.EnvironmentChecker;
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

        CheckerView view = new CheckerView();
        view.printBanner();
        view.printWorkDir(workDir.getAbsolutePath());

        // Step 0: environment checks
        ProcessRunner    envRunner = new ProcessRunner(10);
        EnvironmentChecker envCheck = new EnvironmentChecker(envRunner, view);
        envCheck.check(skipAuthCheck);

        // Step 1: load DSL config
        OopCheckerConfig config;
        try {
            config = ConfigLoader.loadFromDirectory(workDir);
        } catch (Exception e) {
            view.printConfigError(e.getMessage(), e.getCause());
            System.exit(1);
            return;
        }

        view.printConfigLoaded(
                config.getCheckInstruction().getStudentGithubs().size(),
                config.getCheckInstruction().getTaskIds().size());

        // Step 2: run checks
        Path reposDir = workDir.toPath().resolve("repos");
        ProjectChecker checker = new ProjectChecker(config, reposDir, view);
        List<StudentCheckResult> results = checker.runChecks();

        // Step 3: generate HTML report
        HtmlReporter reporter = new HtmlReporter(config);
        String html = reporter.generate(results);

        if (outputFile != null) {
            try (PrintStream out = new PrintStream(outputFile, StandardCharsets.UTF_8)) {
                out.print(html);
            }
            view.printReportSaved(outputFile);
        } else {
            view.printHtml(html);
        }
    }
}

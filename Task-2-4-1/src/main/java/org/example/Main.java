package org.example;

import org.example.checker.ProjectChecker;
import org.example.config.ConfigLoader;
import org.example.model.OopCheckerConfig;
import org.example.model.results.StudentCheckResult;
import org.example.report.HtmlReporter;

import java.io.File;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Entry point. Works like Gradle: looks for oop_checker.groovy in the working directory,
 * reads configuration, runs checks, outputs HTML report to stdout.
 *
 * Usage: java -jar oop-checker.jar [workDir] [--output file.html]
 */
public class Main {
    private static final Logger log = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) throws Exception {
        // Parse args
        File workDir = new File(System.getProperty("user.dir"));
        String outputFile = null;

        for (int i = 0; i < args.length; i++) {
            if ("--output".equals(args[i]) && i + 1 < args.length) {
                outputFile = args[++i];
            } else if (!args[i].startsWith("--")) {
                workDir = new File(args[i]);
            }
        }

        System.err.println("OOP Checker — reading config from: " + workDir.getAbsolutePath());

        // Load config
        OopCheckerConfig config;
        try {
            config = ConfigLoader.loadFromDirectory(workDir);
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            System.exit(1);
            return;
        }

        System.err.println("Config loaded. Checking "
                + config.getCheckInstruction().getStudentGithubs().size() + " student(s), "
                + config.getCheckInstruction().getTaskIds().size() + " task(s).");

        // Run checks
        Path reposDir = workDir.toPath().resolve("repos");
        ProjectChecker checker = new ProjectChecker(config, reposDir);
        List<StudentCheckResult> results = checker.runChecks();

        // Generate HTML report
        HtmlReporter reporter = new HtmlReporter(config);
        String html = reporter.generate(results);

        // Output
        if (outputFile != null) {
            try (PrintStream out = new PrintStream(outputFile, StandardCharsets.UTF_8)) {
                out.print(html);
            }
            System.err.println("Report saved to: " + outputFile);
        } else {
            // Use UTF-8 for stdout (important for Windows)
            PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
            out.print(html);
        }
    }
}

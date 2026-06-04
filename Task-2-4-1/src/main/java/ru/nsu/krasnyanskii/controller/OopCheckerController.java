package ru.nsu.krasnyanskii.controller;

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
 * Orchestrates the full OOP checker pipeline.
 *
 * <p>Responsibilities:
 * <ol>
 *   <li>Parse command-line arguments</li>
 *   <li>Run pre-flight environment checks</li>
 *   <li>Load the Groovy DSL configuration</li>
 *   <li>Run checks for all students</li>
 *   <li>Generate and output the HTML report</li>
 * </ol>
 */
public class OopCheckerController {

    private final File   workDir;
    private final String outputFile;

    /**
     * Creates a controller and parses the given command-line arguments.
     *
     * <p>Recognised arguments:
     * <ul>
     *   <li>{@code --output &lt;path&gt;} — write the HTML report to a file instead of stdout</li>
     *   <li>Any other non-flag argument is treated as the config directory path</li>
     * </ul>
     *
     * @param args command-line arguments passed to {@code main()}
     */
    public OopCheckerController(String[] args) {
        File   dir    = new File(System.getProperty("user.dir"));
        String output = null;

        for (int i = 0; i < args.length; i++) {
            if ("--output".equals(args[i]) && i + 1 < args.length) {
                output = args[++i];
            } else if (!args[i].startsWith("--")) {
                dir = new File(args[i]);
            }
        }

        this.workDir    = dir;
        this.outputFile = output;
    }

    /**
     * Runs the full checker pipeline and outputs the HTML report.
     *
     * @throws Exception if config loading or report writing fails
     */
    public void run() throws Exception {
        CheckerView view = new CheckerView();
        view.printBanner();
        view.printWorkDir(workDir.getAbsolutePath());

        EnvironmentChecker envCheck = new EnvironmentChecker(new ProcessRunner(10), view);
        envCheck.check();

        OopCheckerConfig config = loadConfig(view);

        view.printConfigLoaded(
                config.getCheckInstruction().getStudentGithubs().size(),
                config.getCheckInstruction().getTaskIds().size());

        Path reposDir = workDir.toPath().resolve("repos");
        List<StudentCheckResult> results =
                new ProjectChecker(config, reposDir, view).runChecks();

        String html = new HtmlReporter(config).generate(results);
        writeReport(html, view);
    }

    /**
     * Loads the Groovy DSL configuration from the working directory.
     * Prints an error and exits with code 1 if loading fails.
     *
     * @param view view used to report config errors
     * @return populated configuration
     * @throws Exception if the JVM cannot exit (test environments)
     */
    private OopCheckerConfig loadConfig(CheckerView view) throws Exception {
        try {
            return ConfigLoader.loadFromDirectory(workDir);
        } catch (Exception e) {
            view.printConfigError(e.getMessage(), e.getCause());
            System.exit(1);
            throw e;
        }
    }

    /**
     * Writes the HTML report to a file if {@code --output} was specified,
     * or prints it to stdout otherwise.
     *
     * @param html HTML document to output
     * @param view view used to confirm the saved path
     * @throws Exception if writing to the output file fails
     */
    private void writeReport(String html, CheckerView view) throws Exception {
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

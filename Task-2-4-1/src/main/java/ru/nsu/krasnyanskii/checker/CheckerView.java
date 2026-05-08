package ru.nsu.krasnyanskii.checker;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/**
 * Single point of all console output for the application (SRP).
 * No other class writes to {@code System.out} / {@code System.err} directly.
 */
public class CheckerView {

    private final PrintStream err;
    private final PrintStream out;
    private final boolean verbose;

    /** Uses the standard streams with verbose mode off. */
    public CheckerView() {
        this(System.err, new PrintStream(System.out, true, StandardCharsets.UTF_8), false);
    }

    /**
     * Creates a view with the given streams and verbosity setting.
     *
     * @param err     stream for diagnostics and warnings
     * @param out     stream for the HTML report
     * @param verbose whether to print debug messages
     */
    public CheckerView(PrintStream err, PrintStream out, boolean verbose) {
        this.err     = err;
        this.out     = out;
        this.verbose = verbose;
    }

    /** Prints the startup banner. */
    public void printBanner() {
        err.println("=== OOP Checker ===");
    }

    /**
     * Prints the working directory path.
     *
     * @param path working directory
     */
    public void printWorkDir(String path) {
        err.println("Working dir: " + path);
    }

    /**
     * Prints the number of students and tasks loaded from config.
     *
     * @param students number of students in the config
     * @param tasks    number of tasks in the config
     */
    public void printConfigLoaded(int students, int tasks) {
        err.println("Config loaded: " + students + " student(s), " + tasks + " task(s).");
    }

    /**
     * Prints a config load error with the full cause chain and a usage hint.
     *
     * @param msg   top-level error message
     * @param cause cause chain (may be null)
     */
    public void printConfigError(String msg, Throwable cause) {
        err.println();
        err.println("=== CONFIG LOAD ERROR ===");
        err.println(msg);
        Throwable c = cause;
        while (c != null) {
            err.println("  Caused by: " + c.getMessage());
            c = c.getCause();
        }
        err.println();
        err.println("Hint: pass the directory containing oop_checker.groovy");
        err.println("  ./gradlew run --args=\"example_configs\"");
        err.println("  java -jar oop-checker.jar /path/to/configs");
    }

    /**
     * Prints the path of the saved report file.
     *
     * @param path path to the saved report file
     */
    public void printReportSaved(String path) {
        err.println("Report saved: " + path);
    }

    /**
     * Writes the HTML report to the output stream.
     *
     * @param html full HTML document
     */
    public void printHtml(String html) {
        out.print(html);
    }

    /**
     * Prints the git version string returned by {@code git --version}.
     *
     * @param version output of {@code git --version}
     */
    public void printGitVersion(String version) {
        err.println("Git: " + version);
    }

    /** Warns that git is not available on PATH. */
    public void warnGitNotFound() {
        err.println("WARNING: git not found on PATH. Install git and retry.");
    }

    /**
     * Warns that the git availability check failed.
     *
     * @param msg reason the git check failed
     */
    public void warnGitCheckFailed(String msg) {
        err.println("WARNING: could not check git: " + msg);
    }

    /**
     * Prints the global git user name.
     *
     * @param user value of {@code git config --global user.name}
     */
    public void printGitUser(String user) {
        err.println("Git user: " + user);
    }

    /** Warns that {@code user.name} is not set in the global git config. */
    public void warnGitUserNotSet() {
        err.println("WARNING: git config --global user.name is not set.");
    }

    /**
     * Warns that reading {@code user.name} from the git config failed.
     *
     * @param msg reason the user.name check failed
     */
    public void warnGitUserCheckFailed(String msg) {
        err.println("WARNING: could not read git user.name: " + msg);
    }

    /**
     * Logs a pipeline step for a student's task.
     *
     * @param github student's GitHub login
     * @param taskId task identifier
     * @param step   description of the current pipeline step
     */
    public void infoStep(String github, String taskId, String step) {
        err.println("[" + github + "/" + taskId + "] " + step);
    }

    /**
     * Warns that a requested student was not found in the config.
     *
     * @param github student not found in the config
     */
    public void warnStudentNotFound(String github) {
        err.println("WARNING: Student not found in config: " + github);
    }

    /**
     * Reports a fatal clone failure for a student's repository.
     *
     * @param github student whose repo could not be cloned
     * @param msg    error message
     */
    public void errorCloneFailed(String github, String msg) {
        err.println("ERROR: Failed to clone repo for " + github + ": " + msg);
    }

    /**
     * Warns that a JUnit XML result file could not be parsed.
     *
     * @param path path to the XML test-results file
     * @param msg  parse error message
     */
    public void warnXmlParseFailed(String path, String msg) {
        err.println("WARNING: Could not parse " + path + ": " + msg);
    }

    /**
     * Warns that walking a test-results directory failed.
     *
     * @param dir test-results directory
     * @param msg error message
     */
    public void warnWalkFailed(String dir, String msg) {
        err.println("WARNING: Could not walk " + dir + ": " + msg);
    }

    /**
     * Logs that a repository is being cloned.
     *
     * @param github student whose repo is being cloned
     * @param url    remote repository URL
     */
    public void infoCloning(String github, String url) {
        err.println("Cloning repo for " + github + " from " + url);
    }

    /**
     * Logs that a repository is being updated via fetch/pull.
     *
     * @param github student whose repo is being updated
     */
    public void infoUpdating(String github) {
        err.println("Updating repo for " + github);
    }

    /**
     * Warns that the active-weeks calculation failed.
     *
     * @param msg error message from the active-weeks calculation
     */
    public void warnActiveWeeksFailed(String msg) {
        err.println("WARNING: Failed to get active weeks: " + msg);
    }

    /**
     * Prints git fetch output; no-op unless verbose mode is on.
     *
     * @param fetchOutput raw output from {@code git fetch}
     */
    public void debugFetchOutput(String fetchOutput) {
        if (verbose) {
            err.println("[DEBUG] fetch: " + fetchOutput);
        }
    }

    /**
     * Prints a debug message; no-op unless verbose mode is on.
     *
     * @param msg message to print
     */
    public void debug(String msg) {
        if (verbose) {
            err.println("[DEBUG] " + msg);
        }
    }
}

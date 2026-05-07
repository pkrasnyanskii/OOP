package ru.nsu.krasnyanskii.checker;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/**
 * View layer for the OOP Checker application.
 *
 * <p><b>SRP</b>: single class responsible for all console output.
 * No other class calls {@code System.err} or {@code System.out} directly.</p>
 */
public class CheckerView {

    private final PrintStream err;
    private final PrintStream out;
    private final boolean verbose;

    /** Creates a CheckerView writing to standard streams (non-verbose). */
    public CheckerView() {
        this(System.err, new PrintStream(System.out, true, StandardCharsets.UTF_8), false);
    }

    /**
     * Creates a CheckerView with custom streams and verbosity flag.
     *
     * @param err     stream for diagnostics / warnings
     * @param out     stream for the HTML report
     * @param verbose if true, debug-level messages are printed
     */
    public CheckerView(PrintStream err, PrintStream out, boolean verbose) {
        this.err     = err;
        this.out     = out;
        this.verbose = verbose;
    }

    // ── Startup ──────────────────────────────────────────────────────────────

    /** Prints the application banner. */
    public void printBanner() {
        err.println("=== OOP Checker ===");
    }

    /**
     * Prints the resolved working directory.
     *
     * @param path absolute path of the working directory
     */
    public void printWorkDir(String path) {
        err.println("Working dir: " + path);
    }

    /**
     * Prints how many students and tasks were loaded from the config.
     *
     * @param students number of students
     * @param tasks    number of tasks
     */
    public void printConfigLoaded(int students, int tasks) {
        err.println("Config loaded: " + students + " student(s), " + tasks + " task(s).");
    }

    /**
     * Prints a config load error with hint lines.
     *
     * @param msg   top-level error message
     * @param cause root cause (may be null)
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
     * Prints the path where the HTML report was saved.
     *
     * @param path output file path
     */
    public void printReportSaved(String path) {
        err.println("Report saved: " + path);
    }

    /**
     * Prints the HTML report to stdout.
     *
     * @param html full HTML document
     */
    public void printHtml(String html) {
        out.print(html);
    }

    // ── Environment checks ────────────────────────────────────────────────────

    /**
     * Prints the detected git version string.
     *
     * @param version version output from {@code git --version}
     */
    public void printGitVersion(String version) {
        err.println("Git: " + version);
    }

    /** Warns that git was not found on PATH. */
    public void warnGitNotFound() {
        err.println("WARNING: git not found on PATH. Install git and retry.");
    }

    /** Warns that the git check itself failed. */
    public void warnGitCheckFailed(String msg) {
        err.println("WARNING: could not check git: " + msg);
    }

    /**
     * Prints the configured git user name.
     *
     * @param user value of {@code git config --global user.name}
     */
    public void printGitUser(String user) {
        err.println("Git user: " + user);
    }

    /** Warns that {@code git config --global user.name} is not set. */
    public void warnGitUserNotSet() {
        err.println("WARNING: git config --global user.name is not set.");
    }

    /** Warns that reading the git user name failed. */
    public void warnGitUserCheckFailed(String msg) {
        err.println("WARNING: could not read git user.name: " + msg);
    }

    /**
     * Prints the configured git credential helper.
     *
     * @param helper value of {@code git config --global credential.helper}
     */
    public void printCredentialHelper(String helper) {
        err.println("Git credential.helper: " + helper + " (OK)");
    }

    /** Warns that no credential helper is configured, which may cause password prompts. */
    public void warnCredentialHelperNotSet() {
        err.println();
        err.println("WARNING: git credential.helper is not configured.");
        err.println("  Cloning private repos may hang waiting for a password.");
        err.println("  Use SSH keys, set credential.helper, or --skip-auth-check");
        err.println();
    }

    /** Warns that the credential helper check itself failed. */
    public void warnCredentialHelperCheckFailed(String msg) {
        err.println("WARNING: could not check credential.helper: " + msg);
    }

    // ── ProjectChecker progress ───────────────────────────────────────────────

    /**
     * Prints a pipeline step progress message.
     *
     * @param github student GitHub login
     * @param taskId task identifier
     * @param step   description of the current step
     */
    public void infoStep(String github, String taskId, String step) {
        err.println("[" + github + "/" + taskId + "] " + step);
    }

    /**
     * Warns that a student GitHub login was not found in the config.
     *
     * @param github student GitHub login
     */
    public void warnStudentNotFound(String github) {
        err.println("WARNING: Student not found in config: " + github);
    }

    /**
     * Prints an error when cloning a student repository failed.
     *
     * @param github student GitHub login
     * @param msg    error message
     */
    public void errorCloneFailed(String github, String msg) {
        err.println("ERROR: Failed to clone repo for " + github + ": " + msg);
    }

    /**
     * Warns that a JUnit XML result file could not be parsed.
     *
     * @param path path of the XML file
     * @param msg  error message
     */
    public void warnXmlParseFailed(String path, String msg) {
        err.println("WARNING: Could not parse " + path + ": " + msg);
    }

    /**
     * Warns that the test-results directory could not be traversed.
     *
     * @param dir path of the directory
     * @param msg error message
     */
    public void warnWalkFailed(String dir, String msg) {
        err.println("WARNING: Could not walk " + dir + ": " + msg);
    }

    // ── GitManager progress ───────────────────────────────────────────────────

    /**
     * Prints a message when a student repository is being cloned for the first time.
     *
     * @param github student GitHub login
     * @param url    repository URL
     */
    public void infoCloning(String github, String url) {
        err.println("Cloning repo for " + github + " from " + url);
    }

    /**
     * Prints a message when a student repository is being updated via fetch.
     *
     * @param github student GitHub login
     */
    public void infoUpdating(String github) {
        err.println("Updating repo for " + github);
    }

    /**
     * Warns that the active-weeks git query failed.
     *
     * @param msg error message
     */
    public void warnActiveWeeksFailed(String msg) {
        err.println("WARNING: Failed to get active weeks: " + msg);
    }

    /**
     * Prints raw fetch output at debug level.
     * Only printed when this view was created with {@code verbose = true}.
     *
     * @param fetchOutput output from {@code git fetch}
     */
    public void debugFetchOutput(String fetchOutput) {
        if (verbose) {
            err.println("[DEBUG] fetch: " + fetchOutput);
        }
    }

    /**
     * Prints a debug-level message.
     * Only printed when this view was created with {@code verbose = true}.
     *
     * @param msg debug message
     */
    public void debug(String msg) {
        if (verbose) {
            err.println("[DEBUG] " + msg);
        }
    }
}

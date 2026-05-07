package ru.nsu.krasnyanskii.checker;

import java.io.File;

/**
 * Verifies that the runtime environment is properly configured for git operations.
 *
 * <p><b>SRP</b>: responsible solely for environment pre-flight checks.
 * All output is delegated to {@link CheckerView}.</p>
 *
 * <p><b>DIP</b>: depends on {@link ProcessRunner} and {@link CheckerView} abstractions,
 * not on concrete I/O or process-management implementations.</p>
 */
public class EnvironmentChecker {

    private final ProcessRunner runner;
    private final CheckerView   view;

    /**
     * Creates an EnvironmentChecker.
     *
     * @param runner process runner used for git commands
     * @param view   view for all console output
     */
    public EnvironmentChecker(ProcessRunner runner, CheckerView view) {
        this.runner = runner;
        this.view   = view;
    }

    /**
     * Runs all pre-flight environment checks.
     *
     * @param skipAuthCheck if true, the credential-helper check is skipped
     */
    public void check(boolean skipAuthCheck) {
        checkGitAvailable();
        checkGitUserConfigured();
        if (!skipAuthCheck) {
            warnIfAuthMayPrompt();
        }
    }

    /** Checks that git is available on PATH. */
    private void checkGitAvailable() {
        try {
            ProcessResult r = runner.run(new File(".").toPath(), "git", "--version");
            if (r.isSuccess()) {
                view.printGitVersion(r.getOutput().trim());
            } else {
                view.warnGitNotFound();
            }
        } catch (Exception e) {
            view.warnGitCheckFailed(e.getMessage());
        }
    }

    /** Checks that {@code git config --global user.name} is set. */
    private void checkGitUserConfigured() {
        try {
            ProcessResult r = runner.run(
                    new File(".").toPath(), "git", "config", "--global", "user.name");
            if (r.isSuccess() && !r.getOutput().trim().isEmpty()) {
                view.printGitUser(r.getOutput().trim());
            } else {
                view.warnGitUserNotSet();
            }
        } catch (Exception e) {
            view.warnGitUserCheckFailed(e.getMessage());
        }
    }

    /** Warns if git may prompt for credentials (no credential helper configured). */
    private void warnIfAuthMayPrompt() {
        try {
            ProcessResult r = runner.run(
                    new File(".").toPath(),
                    "git", "config", "--global", "credential.helper");
            boolean hasHelper = r.isSuccess() && !r.getOutput().trim().isEmpty();
            if (!hasHelper) {
                view.warnCredentialHelperNotSet();
            } else {
                view.printCredentialHelper(r.getOutput().trim());
            }
        } catch (Exception e) {
            view.warnCredentialHelperCheckFailed(e.getMessage());
        }
    }
}

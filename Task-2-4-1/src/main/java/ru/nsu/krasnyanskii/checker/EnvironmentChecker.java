package ru.nsu.krasnyanskii.checker;

import java.io.File;

/**
 * Runs pre-flight environment checks before the main pipeline starts:
 * git availability and global user config.
 */
public class EnvironmentChecker {

    private final ProcessRunner runner;
    private final CheckerView   view;

    /**
     * @param runner process runner used to invoke git commands
     * @param view   view for all console output
     */
    public EnvironmentChecker(ProcessRunner runner, CheckerView view) {
        this.runner = runner;
        this.view   = view;
    }

    /** Runs all pre-flight checks. */
    public void check() {
        checkGitAvailable();
        checkGitUserConfigured();
    }

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
}

package ru.nsu.krasnyanskii.checker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

/** Performs git operations (clone, update, log queries) via the console git client. */
public class GitManager {
    private static final Logger log = Logger.getLogger(GitManager.class.getName());
    private static final DateTimeFormatter ISO_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final ProcessRunner runner;
    private final Path reposDir;

    public GitManager(Path reposDir, int timeoutSeconds) {
        this.reposDir = reposDir;
        this.runner = new ProcessRunner(timeoutSeconds);
    }

    /**
     * Verifies that the repository URL is reachable without auth prompts.
     *
     * @param repoUrl repository URL to probe
     * @return true if git ls-remote succeeds
     */
    public boolean verifyGitAuth(String repoUrl) {
        try {
            ProcessResult result = runner.run(reposDir,
                    "git", "ls-remote", "--exit-code", "--heads", repoUrl);
            return result.isSuccess();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Clones the repository if it does not exist locally, or fetches and resets to origin HEAD.
     *
     * @param github  GitHub username (used as the local directory name)
     * @param repoUrl remote repository URL
     * @return path to the local clone
     * @throws IOException          on clone failure
     * @throws InterruptedException if interrupted while waiting for git
     */
    public Path cloneOrUpdate(String github, String repoUrl) throws IOException, InterruptedException {
        Path repoPath = reposDir.resolve(github);

        if (Files.exists(repoPath.resolve(".git"))) {
            log.info("Updating repo for " + github);
            ProcessResult fetch = runner.run(repoPath, "git", "fetch", "--all", "--prune");
            log.fine(fetch.getOutput());
            String branch = detectMainBranch(repoPath);
            runner.run(repoPath, "git", "checkout", branch);
            runner.run(repoPath, "git", "reset", "--hard", "origin/" + branch);
        } else {
            log.info("Cloning repo for " + github + " from " + repoUrl);
            Files.createDirectories(reposDir);
            ProcessResult clone = runner.run(reposDir, "git", "clone", repoUrl, github);
            if (!clone.isSuccess()) {
                throw new IOException("Failed to clone " + repoUrl + ":\n" + clone.getOutput());
            }
        }

        return repoPath;
    }

    /**
     * Returns the date of the last commit that touched the given subdirectory.
     *
     * @param repoPath   local repository path
     * @param taskSubDir subdirectory path relative to the repo root
     * @return last commit date, or null if no commits found
     */
    public LocalDate getLastCommitDate(Path repoPath, String taskSubDir) {
        try {
            ProcessResult result = runner.run(repoPath,
                    "git", "log", "-1", "--format=%ad", "--date=format:%Y-%m-%d", "--", taskSubDir);
            String out = result.getOutput().trim();
            if (out.isEmpty()) {
                return null;
            }
            return LocalDate.parse(out, ISO_DATE);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Returns the set of ISO year-week strings (e.g. "2024-08") for weeks with at least one commit.
     *
     * @param repoPath local repository path
     * @param from     course start date (inclusive)
     * @param to       course end date (inclusive)
     * @return set of active week strings
     */
    public Set<String> getActiveWeeks(Path repoPath, LocalDate from, LocalDate to) {
        Set<String> weeks = new HashSet<>();
        try {
            ProcessResult result = runner.run(repoPath,
                    "git", "log",
                    "--after=" + from.minusDays(1).toString(),
                    "--before=" + to.plusDays(1).toString(),
                    "--format=%ad",
                    "--date=format:%Y-%V");
            for (String line : result.getOutput().split("\n")) {
                String w = line.trim();
                if (!w.isEmpty()) {
                    weeks.add(w);
                }
            }
        } catch (Exception e) {
            log.warning("Failed to get active weeks: " + e.getMessage());
        }
        return weeks;
    }

    private String detectMainBranch(Path repoPath) {
        try {
            ProcessResult result = runner.run(repoPath,
                    "git", "symbolic-ref", "refs/remotes/origin/HEAD");
            String ref = result.getOutput().trim();
            if (ref.endsWith("/main")) {
                return "main";
            }
            if (ref.endsWith("/master")) {
                return "master";
            }
        } catch (Exception e) {
            log.fine("Could not read symbolic-ref: " + e.getMessage());
        }
        try {
            ProcessResult r = runner.run(
                    repoPath, "git", "show-ref", "--verify", "refs/remotes/origin/main");
            if (r.isSuccess()) {
                return "main";
            }
        } catch (Exception e) {
            log.fine("Could not verify origin/main: " + e.getMessage());
        }
        return "master";
    }
}

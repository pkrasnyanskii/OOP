package org.example.checker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

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
     * Verify git is configured without auth prompts (credential helper or SSH keys).
     * Returns true if git is usable.
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
     * Clone or update a student repository. Returns the path to the local clone.
     */
    public Path cloneOrUpdate(String github, String repoUrl) throws IOException, InterruptedException {
        Path repoPath = reposDir.resolve(github);

        if (Files.exists(repoPath.resolve(".git"))) {
            log.info("Updating repo for " + github);
            ProcessResult fetch = runner.run(repoPath, "git", "fetch", "--all", "--prune");
            log.fine(fetch.getOutput());
            // Reset to origin/HEAD (main or master)
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
     * Get date of last commit touching taskSubDir (null if no commits found).
     */
    public LocalDate getLastCommitDate(Path repoPath, String taskSubDir) {
        try {
            ProcessResult result = runner.run(repoPath,
                    "git", "log", "-1", "--format=%ad", "--date=format:%Y-%m-%d", "--", taskSubDir);
            String out = result.getOutput().trim();
            if (out.isEmpty()) return null;
            return LocalDate.parse(out, ISO_DATE);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Return set of "year-week" strings (e.g. "2024-08") for weeks with at least one commit.
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
                if (!w.isEmpty()) weeks.add(w);
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
            if (ref.endsWith("/main")) return "main";
            if (ref.endsWith("/master")) return "master";
        } catch (Exception ignored) {}
        // Fallback: try main, then master
        try {
            ProcessResult r = runner.run(repoPath, "git", "show-ref", "--verify", "refs/remotes/origin/main");
            if (r.isSuccess()) return "main";
        } catch (Exception ignored) {}
        return "master";
    }
}

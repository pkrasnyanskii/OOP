package ru.nsu.krasnyanskii.checker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/** Runs external processes with a timeout and captures their combined output. */
public class ProcessRunner {
    private final int timeoutSeconds;

    public ProcessRunner(int timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }

    /**
     * Runs a command in the given directory.
     *
     * @param workDir working directory for the process
     * @param command command and its arguments
     * @return result containing exit code, output, and timeout flag
     * @throws IOException          if the process cannot be started
     * @throws InterruptedException if the current thread is interrupted while waiting
     */
    public ProcessResult run(Path workDir, String... command) throws IOException, InterruptedException {
        return run(workDir, Arrays.asList(command));
    }

    /**
     * Runs a command in the given directory.
     *
     * @param workDir working directory for the process
     * @param command command and its arguments as a list
     * @return result containing exit code, output, and timeout flag
     * @throws IOException          if the process cannot be started
     * @throws InterruptedException if the current thread is interrupted while waiting
     */
    public ProcessResult run(Path workDir, List<String> command) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.directory(workDir.toFile());
        pb.redirectErrorStream(true);

        Process process = pb.start();
        StringBuilder output = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }

        boolean finished = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);
        if (!finished) {
            process.destroyForcibly();
            return new ProcessResult(-1, output + "\n[PROCESS TIMED OUT after " + timeoutSeconds + "s]", true);
        }

        return new ProcessResult(process.exitValue(), output.toString(), false);
    }

    /**
     * Returns the platform-specific Gradle wrapper command name.
     *
     * @return "gradlew.bat" on Windows, "./gradlew" elsewhere
     */
    public static String gradlewCommand() {
        return System.getProperty("os.name", "").toLowerCase().contains("win") ? "gradlew.bat" : "./gradlew";
    }
}

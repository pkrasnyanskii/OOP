package ru.nsu.krasnyanskii.config;

import groovy.lang.Binding;
import groovy.lang.Script;
import java.io.File;
import java.io.IOException;
import org.codehaus.groovy.control.CompilerConfiguration;
import ru.nsu.krasnyanskii.dsl.OopCheckerScriptInterface;
import ru.nsu.krasnyanskii.model.OopCheckerConfig;

/**
 * Loads configuration from a Groovy DSL file (oop_checker.groovy).
 * Works like Gradle: finds the script in the working directory, executes it,
 * and returns the populated config object.
 */
public class ConfigLoader {
    private static final String SCRIPT_NAME = "oop_checker.groovy";

    /**
     * Searches for oop_checker.groovy in the given directory and loads it.
     *
     * @param directory directory to search in
     * @return populated configuration
     * @throws IOException if the file is not found or the script fails
     */
    public static OopCheckerConfig loadFromDirectory(File directory) throws IOException {
        File scriptFile = new File(directory, SCRIPT_NAME);
        if (!scriptFile.exists()) {
            throw new IOException(
                "Config file '" + SCRIPT_NAME + "' not found in "
                    + directory.getAbsolutePath()
                    + "\nCreate oop_checker.groovy in the working directory."
            );
        }
        return loadFromFile(scriptFile);
    }

    /**
     * Loads configuration from a specific Groovy DSL file.
     *
     * @param scriptFile the DSL file to execute
     * @return populated configuration
     * @throws IOException if the script cannot be parsed or executed
     */
    public static OopCheckerConfig loadFromFile(File scriptFile) throws IOException {
        try {
            CompilerConfiguration cc = new CompilerConfiguration();
            // Reference the Groovy class by name to avoid a Java to Groovy compile-time dependency
            cc.setScriptBaseClass("ru.nsu.krasnyanskii.dsl.OopCheckerScript");

            groovy.lang.GroovyShell shell = new groovy.lang.GroovyShell(
                    ConfigLoader.class.getClassLoader(),
                    new Binding(),
                    cc
            );

            Script script = shell.parse(scriptFile);

            // Access via Java interface to avoid a compile-time dependency on the Groovy class
            OopCheckerScriptInterface dslScript = (OopCheckerScriptInterface) script;
            dslScript.setScriptDir(scriptFile.getParentFile());

            script.run();

            return dslScript.getConfig();

        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException(
                "Error loading config from " + scriptFile.getAbsolutePath()
                    + ": " + e.getMessage(),
                e
            );
        }
    }
}

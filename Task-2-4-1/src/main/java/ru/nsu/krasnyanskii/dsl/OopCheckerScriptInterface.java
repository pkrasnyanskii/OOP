package ru.nsu.krasnyanskii.dsl;

import java.io.File;
import ru.nsu.krasnyanskii.model.OopCheckerConfig;

/**
 * Java interface implemented by OopCheckerScript.groovy.
 * Allows ConfigLoader (Java) to work with the Groovy DSL class
 * without a compile-time Java to Groovy dependency.
 */
public interface OopCheckerScriptInterface {

    OopCheckerConfig getConfig();

    void setConfig(OopCheckerConfig config);

    void setScriptDir(File dir);
}

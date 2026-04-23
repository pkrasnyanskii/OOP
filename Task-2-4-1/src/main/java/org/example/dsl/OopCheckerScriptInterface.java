package org.example.dsl;

import org.example.model.OopCheckerConfig;

import java.io.File;

/**
 * Java-интерфейс для DSL-скрипта.
 * OopCheckerScript.groovy реализует этот интерфейс, что позволяет
 * Java-коду (ConfigLoader) работать с классом, написанным на Groovy,
 * не создавая циклической зависимости при компиляции.
 */
public interface OopCheckerScriptInterface {
    OopCheckerConfig getConfig();
    void setConfig(OopCheckerConfig config);
    void setScriptDir(File dir);
}

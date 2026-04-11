package org.example.config;

import groovy.lang.Binding;
import groovy.lang.Script;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.example.dsl.OopCheckerScriptInterface;
import org.example.model.OopCheckerConfig;

import java.io.File;
import java.io.IOException;

/**
 * Загружает конфигурацию из Groovy DSL-файла (oop_checker.groovy).
 * Работает аналогично Gradle: ищет скрипт в рабочей директории,
 * выполняет его и возвращает заполненный объект конфигурации.
 */
public class ConfigLoader {
    private static final String SCRIPT_NAME = "oop_checker.groovy";

    /** Ищет oop_checker.groovy в указанной директории. */
    public static OopCheckerConfig loadFromDirectory(File directory) throws IOException {
        File scriptFile = new File(directory, SCRIPT_NAME);
        if (!scriptFile.exists()) {
            throw new IOException(
                "Конфигурационный файл '" + SCRIPT_NAME + "' не найден в " + directory.getAbsolutePath()
                + "\nСоздайте файл oop_checker.groovy в рабочей директории."
            );
        }
        return loadFromFile(scriptFile);
    }

    /** Загружает конфигурацию из конкретного файла. */
    public static OopCheckerConfig loadFromFile(File scriptFile) throws IOException {
        try {
            CompilerConfiguration cc = new CompilerConfiguration();
            // Используем имя класса как строку — избегаем compile-time зависимости Java → Groovy
            cc.setScriptBaseClass("org.example.dsl.OopCheckerScript");

            groovy.lang.GroovyShell shell = new groovy.lang.GroovyShell(
                    ConfigLoader.class.getClassLoader(),
                    new Binding(),
                    cc
            );

            Script script = shell.parse(scriptFile);

            // Доступ через интерфейс (Java-интерфейс + Groovy-реализация)
            OopCheckerScriptInterface dslScript = (OopCheckerScriptInterface) script;
            dslScript.setScriptDir(scriptFile.getParentFile());

            script.run();

            return dslScript.getConfig();

        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException(
                "Ошибка при загрузке конфигурации из " + scriptFile.getAbsolutePath()
                + ": " + e.getMessage(), e
            );
        }
    }
}

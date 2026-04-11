package org.example.dsl

import groovy.lang.Script
import org.codehaus.groovy.control.CompilerConfiguration
import org.example.dsl.OopCheckerScriptInterface
import org.example.dsl.builders.*
import org.example.model.OopCheckerConfig

/**
 * Базовый класс для всех oop_checker.groovy скриптов.
 *
 * Реализует OopCheckerScriptInterface, чтобы Java-код (ConfigLoader)
 * мог работать с ним без compile-time зависимости на Groovy-класс.
 *
 * Предоставляет DSL-методы верхнего уровня:
 *   tasks, groups, check, checkPoints, settings, importConfig
 */
abstract class OopCheckerScript extends Script implements OopCheckerScriptInterface {

    @Override
    OopCheckerConfig getConfig() { return config }

    @Override
    void setConfig(OopCheckerConfig config) { this.config = config }

    @Override
    void setScriptDir(File dir) { this.scriptDir = dir }

    OopCheckerConfig config = new OopCheckerConfig()
    File scriptDir

    // ── Импорт вложенных конфиг-файлов ─────────────────────────────────────

    /**
     * Импортирует другой Groovy-конфиг (путь — относительно текущего файла).
     *
     * Пример:
     *   importConfig "tasks.groovy"
     *   importConfig "students.groovy"
     */
    void importConfig(String filePath) {
        File importFile = new File(filePath)
        if (!importFile.isAbsolute()) {
            importFile = new File(scriptDir ?: new File('.'), filePath)
        }

        CompilerConfiguration cc = new CompilerConfiguration()
        cc.setScriptBaseClass(OopCheckerScript.class.getName())

        def shell = new groovy.lang.GroovyShell(
                OopCheckerScript.class.classLoader,
                new groovy.lang.Binding(),
                cc
        )

        Script imported = shell.parse(importFile)
        OopCheckerScriptInterface importedDsl = (OopCheckerScriptInterface) imported
        importedDsl.setConfig(this.config)         // разделяем один объект конфигурации
        importedDsl.setScriptDir(importFile.parentFile)
        imported.run()
    }

    // ── DSL-методы верхнего уровня ─────────────────────────────────────────

    void tasks(Closure closure) {
        def builder = new TasksBuilder(config)
        closure.delegate = builder
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.call()
    }

    void groups(Closure closure) {
        def builder = new GroupsBuilder(config)
        closure.delegate = builder
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.call()
    }

    void check(Closure closure) {
        def builder = new CheckBuilder(config)
        closure.delegate = builder
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.call()
    }

    void checkPoints(Closure closure) {
        def builder = new CheckPointsBuilder(config)
        closure.delegate = builder
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.call()
    }

    void settings(Closure closure) {
        def builder = new SettingsBuilder(config)
        closure.delegate = builder
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.call()
    }
}

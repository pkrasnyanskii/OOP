package ru.nsu.krasnyanskii.dsl

import groovy.lang.Script
import org.codehaus.groovy.control.CompilerConfiguration
import ru.nsu.krasnyanskii.dsl.OopCheckerScriptInterface
import ru.nsu.krasnyanskii.dsl.builders.*
import ru.nsu.krasnyanskii.model.OopCheckerConfig

/**
 * Base class for all oop_checker.groovy scripts.
 * Implements OopCheckerScriptInterface so Java code (ConfigLoader)
 * can access it without a compile-time dependency on this Groovy class.
 * Provides top-level DSL methods: tasks, groups, check, checkPoints, settings, importConfig.
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

    /**
     * Imports another Groovy config file relative to the current script's directory.
     * The imported script shares the same OopCheckerConfig instance.
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
        importedDsl.setConfig(this.config)
        importedDsl.setScriptDir(importFile.parentFile)
        imported.run()
    }

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

plugins {
    id("java")
    id("groovy")
    id("jacoco")
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

java {
    // Компилируем под Java 17, запускается на JVM 17+.
    // Groovy 4 поддерживает Java 17–23.
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

application {
    mainClass.set("org.example.Main")
    // Дефолтные аргументы для ./gradlew run — указываем папку с примером конфига.
    // Для запуска с другим конфигом: ./gradlew run --args="/path/to/config"
    applicationDefaultJvmArgs = listOf(
        // Groovy 4 использует рефлексию; Java 9+ требует явного открытия модулей.
        "--add-opens=java.base/java.lang=ALL-UNNAMED",
        "--add-opens=java.base/java.lang.reflect=ALL-UNNAMED",
        "--add-opens=java.base/java.util=ALL-UNNAMED",
        "--add-opens=java.base/java.io=ALL-UNNAMED"
    )
}

// Для ./gradlew run — по умолчанию ищем конфиг в example_configs/
tasks.named<JavaExec>("run") {
    args = listOf("example_configs")
    // Вывод в консоль без буферизации (видно ошибки сразу)
    standardOutput = System.out
    errorOutput    = System.err
}

dependencies {
    // Groovy 4 runtime for DSL.
    // Groovy 3 не поддерживает Java 23+ (major version 67) — нужна версия 4.
    // Артефакты: org.codehaus.groovy → org.apache.groovy (пакеты классов не изменились).
    implementation("org.apache.groovy:groovy:4.0.22")
    implementation("org.apache.groovy:groovy-xml:4.0.22")

    // Lombok — annotation processor, убирает boilerplate из Java model-классов
    compileOnly("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")
    testCompileOnly("org.projectlombok:lombok:1.18.32")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.32")

    // Testing
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
    jvmArgs(
        "--add-opens=java.base/java.lang=ALL-UNNAMED",
        "--add-opens=java.base/java.lang.reflect=ALL-UNNAMED",
        "--add-opens=java.base/java.util=ALL-UNNAMED",
        "--add-opens=java.base/java.io=ALL-UNNAMED"
    )
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "org.example.Main"
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

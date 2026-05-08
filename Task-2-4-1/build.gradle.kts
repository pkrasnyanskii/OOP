plugins {
    id("java")
    id("groovy")
    id("jacoco")
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

application {
    mainClass.set("ru.nsu.krasnyanskii.Main")
    applicationDefaultJvmArgs = listOf(
        "--add-opens=java.base/java.lang=ALL-UNNAMED",
        "--add-opens=java.base/java.lang.reflect=ALL-UNNAMED",
        "--add-opens=java.base/java.util=ALL-UNNAMED",
        "--add-opens=java.base/java.io=ALL-UNNAMED"
    )
}

tasks.named<JavaExec>("run") {
    args = listOf("example_configs")
    standardOutput = System.out
    errorOutput    = System.err
}

dependencies {
    implementation("org.apache.groovy:groovy:4.0.22")
    implementation("org.apache.groovy:groovy-xml:4.0.22")

    compileOnly("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")
    testCompileOnly("org.projectlombok:lombok:1.18.32")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.32")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

val jvmOpenArgs = listOf(
    "--add-opens=java.base/java.lang=ALL-UNNAMED",
    "--add-opens=java.base/java.lang.reflect=ALL-UNNAMED",
    "--add-opens=java.base/java.util=ALL-UNNAMED",
    "--add-opens=java.base/java.io=ALL-UNNAMED"
)

tasks.test {
    useJUnitPlatform {
        excludeTags("integration")
    }
    finalizedBy(tasks.jacocoTestReport)
    jvmArgs(jvmOpenArgs)
}

tasks.register<Test>("integrationTest") {
    description = "Integration tests that clone real repositories. Run manually."
    group = "verification"
    useJUnitPlatform {
        includeTags("integration")
    }
    jvmArgs(jvmOpenArgs)
    systemProperty("junit.jupiter.execution.timeout.default", "5m")
    testLogging {
        showStandardStreams = true
        events("passed", "failed", "skipped")
    }
}

tasks.javadoc {
    (options as StandardJavadocDocletOptions).addStringOption("Xdoclint:none", "-quiet")
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
        attributes["Main-Class"] = "ru.nsu.krasnyanskii.Main"
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

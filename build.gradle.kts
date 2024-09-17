import net.ltgt.gradle.errorprone.errorprone

plugins {
    `java-library`
    `maven-publish`
    id("net.ltgt.errorprone") version "4.0.1"
    id("com.adarshr.test-logger") version "4.0.0"
}

group = "com.github.gavlyukovskiy"
if (version == "unspecified") {
    version = "0.1.0-SNAPSHOT"
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
    withJavadocJar()
    withSourcesJar()
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.checkerframework:checker:3.47.0")
    implementation("org.checkerframework:checker-qual:3.47.0")
    implementation("com.google.errorprone:error_prone_check_api:2.32.0")
    implementation("org.jspecify:jspecify:1.0.0")
    compileOnly("org.springframework:spring-context:6.1.13")

    compileOnly("com.google.errorprone:javac:9+181-r4173-1")

    // Testing
    testImplementation(platform("org.junit:junit-bom:5.11.0"))
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testImplementation("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.springframework:spring-context:6.1.13")
    testImplementation("org.assertj:assertj-core:3.26.3")
    testImplementation("org.checkerframework:framework-test:3.47.0") {
        exclude(group = "junit", module = "junit")
    }
    testImplementation("com.google.errorprone:error_prone_test_helpers:2.32.0")

    errorprone("com.google.errorprone:error_prone_core:2.32.0")
    errorprone("com.uber.nullaway:nullaway:0.11.3")
}

// Add `mavenLocal()` in `repositories`, then run `./gradlew publishToMavenLocal`
// to publish your checker to your local Maven repository.
publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "org.checkerframework"
            artifactId = "templatefora-checker"
            version = "0.1-SNAPSHOT"

            from(components["java"])
        }
    }
}

tasks {
    clean {
        doFirst {
            delete("${rootDir}/tests/build/")
        }
    }

    withType<JavaCompile>().configureEach {
        options.compilerArgs.add("-Xlint:all")
        options.compilerArgs.add("-Werror")
        options.encoding = "UTF-8"
        options.errorprone {
            error(
                "NullAway",
                "RemoveUnusedImports",
                "WildcardImport",
                "RedundantThrows",
                "RedundantOverride",
                "SystemOut",
                "UnnecessarilyFullyQualified",
                "UnnecessarilyUsedValue",
                "UnnecessaryFinal",
                "UnusedException"
            )
            option("NullAway:JSpecifyMode")
            option("NullAway:AnnotatedPackages", "com.github.gavlyukovskiy")
        }
    }

    test {
        useJUnitPlatform()
        jvmArgs(
            // to be kept in sync with https://checkerframework.org/manual/#javac-jdk11
            listOf(
                // These are required in Java 16+ because the --illegal-access option is set to deny
                // by default. None of these packages are accessed via reflection, so the module
                // only needs to be exported, but not opened.
                "--add-exports", "jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED",
                "--add-exports", "jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED",
                "--add-exports", "jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED",
                "--add-exports", "jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED",
                "--add-exports", "jdk.compiler/com.sun.tools.javac.model=ALL-UNNAMED",
                "--add-exports", "jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED",
                "--add-exports", "jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED",
                "--add-exports", "jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED",
                "--add-exports", "jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED",
                "--add-exports", "jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED",
                // Required because the Checker Framework reflectively accesses private members in com.sun.tools.javac.comp.
                "--add-opens", "jdk.compiler/com.sun.tools.javac.comp=ALL-UNNAMED"
            )
        )
    }
}

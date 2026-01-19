plugins {
    java
    `java-library`
    `maven-publish`
    id("com.adarshr.test-logger") version "4.0.0"
    id("com.github.ben-manes.versions") version "0.53.0"
    id("com.diffplug.spotless") version "8.1.0"
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")
    apply(plugin = "com.adarshr.test-logger")
    apply(plugin = "com.github.ben-manes.versions")
    apply(plugin = "com.diffplug.spotless")

    group = "com.github.toastshaman.tinytypes"
    version = project.findProperty("version") ?: "0.0.1"

    repositories {
        mavenCentral()
    }

    java {
        withSourcesJar()
        sourceCompatibility = JavaVersion.VERSION_25
        targetCompatibility = JavaVersion.VERSION_25
        toolchain {
            languageVersion = JavaLanguageVersion.of(25)
        }
    }

    tasks.withType<JavaCompile> {
        finalizedBy(tasks.spotlessApply)
    }

    tasks.named<Test>("test") {
        useJUnitPlatform()
    }

    spotless {
        java {
            palantirJavaFormat("2.85.0")
            toggleOffOn()
        }
    }

    publishing {
        repositories {
            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/ToastShaman/tiny-types")
                credentials {
                    username = System.getenv("GITHUB_ACTOR")
                    password = System.getenv("GITHUB_TOKEN")
                }
            }
        }
    }
}

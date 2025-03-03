plugins {
    java
    `java-library`
    `maven-publish`
    id("com.adarshr.test-logger") version "4.0.0"
    id("com.github.ben-manes.versions") version "0.52.0"
    id("com.diffplug.spotless") version "7.0.2"
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
        sourceCompatibility = JavaVersion.VERSION_23
        targetCompatibility = JavaVersion.VERSION_23
        toolchain {
            languageVersion = JavaLanguageVersion.of(23)
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
            palantirJavaFormat("2.55.0")
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

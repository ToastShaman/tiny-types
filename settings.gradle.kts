rootProject.name = "tiny-types"

include("core")
include("format-jackson")
include("events")
include("events-format-jackson")
include("events-visualiser-mermaid")
include("fp")
include("fp-database-jooq")
include("fp-database-spring-jdbc")
include("time")
include("testing-core")
include("testing-events")
include("testing-fp")
include("testing-faker")

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            version("assertj", "3.25.3")
            version("jackson", "2.16.1")
            version("junit", "5.10.2")
            version("slf4j", "2.0.9")
            version("vavr", "0.10.4")

            library("json","org.json:json:20230618")
            library("json-path", "com.jayway.jsonpath:json-path:2.9.0")
            library("vavr", "io.vavr", "vavr").versionRef("vavr")
            library("slf4j-api", "org.slf4j", "slf4j-api").versionRef("slf4j")
            library("slf4j-simple", "org.slf4j", "slf4j-simple").versionRef("slf4j")
            library("jackson-databind", "com.fasterxml.jackson.core", "jackson-databind").versionRef("jackson")
            library("jackson-datatype-json", "com.fasterxml.jackson.datatype", "jackson-datatype-json-org").versionRef("jackson")
            library("jackson-datatype-jdk8", "com.fasterxml.jackson.datatype", "jackson-datatype-jdk8").versionRef("jackson")
            library("jackson-datatype-jsr310", "com.fasterxml.jackson.datatype", "jackson-datatype-jsr310").versionRef("jackson")

            library("h2", "com.h2database:h2:2.2.224")
            library("jooq", "org.jooq:jooq:3.19.3")
            library("spring-jdbc", "org.springframework:spring-jdbc:6.1.3")
            library("flyway", "org.flywaydb:flyway-core:10.7.2")
            library("hikari", "com.zaxxer:HikariCP:5.1.0")
            library("guava", "com.google.guava:guava:33.0.0-jre")

            library("junit-jupiter", "org.junit.jupiter", "junit-jupiter").versionRef("junit")
            library("junit-jupiter-params", "org.junit.jupiter", "junit-jupiter-params").versionRef("junit")
            library("assertj-core", "org.assertj", "assertj-core").versionRef("assertj")
            library("assertj-json", "net.javacrumbs.json-unit:json-unit-assertj:3.2.4")
            library("assertj-vavr", "org.assertj:assertj-vavr:0.4.3")
            library("okeydoke", "com.oneeyedmen:okeydoke:2.0.3")

            library("datafaker", "net.datafaker:datafaker:2.1.0")
            library("ulid", "com.github.f4b6a3:ulid-creator:5.2.3")
        }
    }
}
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
            version("assertj", "3.27.3")
            version("jackson", "2.19.1")
            version("junit", "5.13.0")
            version("slf4j", "2.0.9")
            version("vavr", "0.10.6")

            library("json","org.json:json:20250517")
            library("json-path", "com.jayway.jsonpath:json-path:2.9.0")
            library("vavr", "io.vavr", "vavr").versionRef("vavr")
            library("slf4j-api", "org.slf4j", "slf4j-api").versionRef("slf4j")
            library("slf4j-simple", "org.slf4j", "slf4j-simple").versionRef("slf4j")
            library("jackson-databind", "com.fasterxml.jackson.core", "jackson-databind").versionRef("jackson")
            library("jackson-datatype-json", "com.fasterxml.jackson.datatype", "jackson-datatype-json-org").versionRef("jackson")
            library("jackson-datatype-jdk8", "com.fasterxml.jackson.datatype", "jackson-datatype-jdk8").versionRef("jackson")
            library("jackson-datatype-jsr310", "com.fasterxml.jackson.datatype", "jackson-datatype-jsr310").versionRef("jackson")

            library("h2", "com.h2database:h2:2.3.232")
            library("jooq", "org.jooq:jooq:3.20.5")
            library("spring-jdbc", "org.springframework:spring-jdbc:6.2.7")
            library("flyway", "org.flywaydb:flyway-core:11.10.1")
            library("hikari", "com.zaxxer:HikariCP:6.3.0")
            library("guava", "com.google.guava:guava:33.4.8-jre")

            library("junit-jupiter", "org.junit.jupiter", "junit-jupiter").versionRef("junit")
            library("junit-jupiter-params", "org.junit.jupiter", "junit-jupiter-params").versionRef("junit")
            library("assertj-core", "org.assertj", "assertj-core").versionRef("assertj")
            library("assertj-json", "net.javacrumbs.json-unit:json-unit-assertj:4.1.1")
            library("assertj-vavr", "org.assertj:assertj-vavr:0.4.3")
            library("okeydoke", "com.oneeyedmen:okeydoke:2.0.3")

            library("datafaker", "net.datafaker:datafaker:2.4.3")
            library("ulid", "com.github.f4b6a3:ulid-creator:5.2.3")
        }
    }
}
rootProject.name = "tiny-types"

include("core")
include("aws-sqs-connector")
include("format-jackson")
include("events")
include("events-format-jackson")
include("events-visualiser-mermaid")
include("fp")
include("fp-database-jooq")
include("fp-database-spring-jdbc")
include("http-client-okhttp")
include("time")
include("testing-core")
include("testing-events")
include("testing-fp")
include("testing-faker")

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            version("assertj", "3.27.3")
            version("jackson", "2.20.0")
            version("junit", "6.0.0")
            version("slf4j", "2.0.9")
            version("vavr", "0.10.6")
            version("okhttp", "5.2.1")
            version("aws", "2.35.10")
            version("record-builder", "49")
            version("testcontainers", "2.0.1")

            library("json", "org.json:json:20250517")
            library("json-path", "com.jayway.jsonpath:json-path:2.9.0")
            library("vavr", "io.vavr", "vavr").versionRef("vavr")
            library("slf4j-api", "org.slf4j", "slf4j-api").versionRef("slf4j")
            library("slf4j-simple", "org.slf4j", "slf4j-simple").versionRef("slf4j")
            library("jackson-databind", "com.fasterxml.jackson.core", "jackson-databind").versionRef("jackson")
            library("jackson-datatype-json", "com.fasterxml.jackson.datatype", "jackson-datatype-json-org").versionRef("jackson")
            library("jackson-datatype-jdk8", "com.fasterxml.jackson.datatype", "jackson-datatype-jdk8").versionRef("jackson")
            library("jackson-datatype-jsr310", "com.fasterxml.jackson.datatype", "jackson-datatype-jsr310").versionRef("jackson")

            library("h2", "com.h2database:h2:2.4.240")
            library("jooq", "org.jooq:jooq:3.20.8")
            library("spring-jdbc", "org.springframework:spring-jdbc:6.2.7")
            library("flyway", "org.flywaydb:flyway-core:11.14.1")
            library("hikari", "com.zaxxer:HikariCP:7.0.2")
            library("guava", "com.google.guava:guava:33.5.0-jre")

            library("okhttp", "com.squareup.okhttp3", "okhttp").versionRef("okhttp")
            library("okhttp-logging-interceptor", "com.squareup.okhttp3", "logging-interceptor").versionRef("okhttp")
            library("okhttp-mockwebserver", "com.squareup.okhttp3", "mockwebserver").versionRef("okhttp")
            library("failsafe", "dev.failsafe:failsafe:3.3.2")

            library("aws-sqs", "software.amazon.awssdk", "sqs").versionRef("aws")

            library("record-builder-core", "io.soabase.record-builder", "record-builder-core").versionRef("record-builder")
            library("record-builder-processor", "io.soabase.record-builder", "record-builder-processor").versionRef("record-builder")

            library("junit-jupiter", "org.junit.jupiter", "junit-jupiter").versionRef("junit")
            library("junit-jupiter-params", "org.junit.jupiter", "junit-jupiter-params").versionRef("junit")
            library("assertj-core", "org.assertj", "assertj-core").versionRef("assertj")
            library("assertj-json", "net.javacrumbs.json-unit:json-unit-assertj:5.0.0")
            library("assertj-vavr", "org.assertj:assertj-vavr:0.4.3")
            library("okeydoke", "com.oneeyedmen:okeydoke:2.0.3")

            library("datafaker", "net.datafaker:datafaker:2.5.2")
            library("ulid", "com.github.f4b6a3:ulid-creator:5.2.3")
            library("awaitility", "org.awaitility:awaitility:4.3.0")

            library("testcontainers-junit", "org.testcontainers", "testcontainers-junit-jupiter").versionRef("testcontainers")
            library("testcontainers-mongodb", "org.testcontainers", "testcontainers-mongodb").versionRef("testcontainers")
            library("testcontainers-localstack", "org.testcontainers", "testcontainers-localstack").versionRef("testcontainers")

        }
    }
}
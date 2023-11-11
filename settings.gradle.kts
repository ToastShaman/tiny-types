rootProject.name = "tiny-types"

include("core")
include("format-jackson")
include("events")
include("events-format-jackson")
include("fp")
include("time")
include("testing-core")
include("testing-events")

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            version("assertj", "3.24.2")
            version("jackson", "2.15.2")
            version("junit", "5.10.1")
            version("slf4j", "2.0.9")

            library("json","org.json:json:20230618")
            library("vavr", "io.vavr:vavr:0.10.4")
            library("slf4j-api", "org.slf4j", "slf4j-api").versionRef("slf4j")
            library("slf4j-simple", "org.slf4j", "slf4j-simple").versionRef("slf4j")
            library("jackson-databind", "com.fasterxml.jackson.core", "jackson-databind").versionRef("jackson")
            library("jackson-datatype-json", "com.fasterxml.jackson.datatype", "jackson-datatype-json-org").versionRef("jackson")

            library("junit-jupiter", "org.junit.jupiter", "junit-jupiter").versionRef("junit")
            library("junit-jupiter-params", "org.junit.jupiter", "junit-jupiter-params").versionRef("junit")
            library("assertj-core", "org.assertj", "assertj-core").versionRef("assertj")
            library("assertj-json", "net.javacrumbs.json-unit:json-unit-assertj:3.2.2")
        }
    }
}
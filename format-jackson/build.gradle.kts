dependencies {
    api(project(":core"))
    api(libs.jackson.databind)
    api(libs.jackson.datatype.jdk8)
    api(libs.jackson.datatype.jsr310)
    api(libs.jackson.datatype.json)
    api(libs.json.path)

    testImplementation(libs.assertj.core)
    testImplementation(libs.assertj.json)
    testImplementation(libs.assertj.vavr)

    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.jupiter.params)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "format-jackson"
            from(components["java"])
        }
    }
}
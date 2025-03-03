dependencies {
    api(libs.vavr)
    api(libs.slf4j.api)

    testImplementation(project(":testing-events"))
    testImplementation(libs.assertj.core)
    testImplementation(libs.assertj.json)

    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.jupiter.params)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    testImplementation(libs.jackson.datatype.json)
    testImplementation(libs.slf4j.simple)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "events"
            from(components["java"])
        }
    }
}
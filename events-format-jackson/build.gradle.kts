dependencies {
    api(project(":events"))
    api(libs.jackson.databind)

    testImplementation(libs.assertj.core)
    testImplementation(libs.assertj.json)

    testImplementation(libs.jackson.datatype.json)
    testImplementation(libs.slf4j.simple)

    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.jupiter.params)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "events-format-jackson"
            from(components["java"])
        }
    }
}
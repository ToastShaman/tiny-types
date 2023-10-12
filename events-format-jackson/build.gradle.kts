dependencies {
    api(project(":events"))
    api(libs.jackson.databind)

    testImplementation(libs.assertj.core)
    testImplementation(libs.assertj.json)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.jupiter.params)

    testImplementation(libs.jackson.datatype.json)
    testImplementation(libs.slf4j.simple)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "events-format-jackson"
            from(components["java"])
        }
    }
}
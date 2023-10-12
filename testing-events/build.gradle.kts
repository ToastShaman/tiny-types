dependencies {
    api(project(":events"))
    api(libs.assertj.core)

    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.jupiter.params)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "testing-events"
            from(components["java"])
        }
    }
}

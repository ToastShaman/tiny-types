dependencies {
    api(project(":events"))
    api(libs.json)

    testImplementation(libs.assertj.core)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.jupiter.params)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "events-visualiser-mermaid"
            from(components["java"])
        }
    }
}
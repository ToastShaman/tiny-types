dependencies {
    api(project(":events"))
    api(libs.json)
    implementation(libs.guava)

    testImplementation(libs.assertj.core)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.jupiter.params)
    testImplementation(libs.okeydoke)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "events-visualiser-mermaid"
            from(components["java"])
        }
    }
}
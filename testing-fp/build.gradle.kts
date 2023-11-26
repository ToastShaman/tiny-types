dependencies {
    api(project(":fp"))
    api(libs.assertj.core)

    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.jupiter.params)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "testing-fp"
            from(components["java"])
        }
    }
}

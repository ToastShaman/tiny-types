dependencies {
    api(project(":core"))
    api(libs.assertj.core)

    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.jupiter.params)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "testing-core"
            from(components["java"])
        }
    }
}

dependencies {
    api(libs.datafaker)

    testImplementation(libs.assertj.core)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.jupiter.params)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "testing-faker"
            from(components["java"])
        }
    }
}

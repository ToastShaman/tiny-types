dependencies {
    api(libs.vavr)

    testImplementation(libs.assertj.core)
    testImplementation(libs.assertj.vavr)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.jupiter.params)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "fp"
            from(components["java"])
        }
    }
}
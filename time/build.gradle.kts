dependencies {
    testImplementation(libs.assertj.core)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.jupiter.params)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "time"
            from(components["java"])
        }
    }
}
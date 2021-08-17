dependencies {
    api(libs.vavr)

    testImplementation(libs.assertj.core)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.jupiter.params)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "tiny-types-fp"
            from(components["java"])
        }
    }
}
dependencies {
    api(libs.datafaker)
    api(libs.ulid)

    testImplementation(libs.assertj.core)

    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.jupiter.params)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "testing-faker"
            from(components["java"])
        }
    }
}

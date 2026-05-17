dependencies {
    api(libs.junit.jupiter)
    api(libs.opentest4j)

    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.jupiter.params)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "testing-approvals"
            from(components["java"])
        }
    }
}

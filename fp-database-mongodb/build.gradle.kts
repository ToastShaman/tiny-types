dependencies {
    api(libs.mongodb.driver.sync)

    testImplementation(libs.slf4j.simple)
    testImplementation(libs.assertj.core)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.jupiter.params)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    testImplementation(libs.testcontainers.mongodb)
    testImplementation(libs.testcontainers.junit)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "fp-database-mongodb"
            from(components["java"])
        }
    }
}

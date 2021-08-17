dependencies {
    api(project(":core"))
    implementation(libs.jackson.databind)

    testImplementation(libs.assertj.core)
    testImplementation(libs.assertj.json)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.jupiter.params)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "tiny-types-format-jackson"
            from(components["java"])
        }
    }
}
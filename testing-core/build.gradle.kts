dependencies {
    api(project(":core"))
    api(libs.assertj.core)

    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.jupiter.params)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "tiny-types-testing"
            from(components["java"])
        }
    }
}

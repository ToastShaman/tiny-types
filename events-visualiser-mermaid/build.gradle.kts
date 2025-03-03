dependencies {
    api(project(":events"))
    api(libs.json)
    implementation(libs.guava)

    testImplementation(libs.assertj.core)
    testImplementation(libs.okeydoke)

    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.jupiter.params)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "events-visualiser-mermaid"
            from(components["java"])
        }
    }
}
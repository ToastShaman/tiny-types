dependencies {
    api(libs.vavr)
    api(libs.okhttp)
    api(libs.okhttp.logging.interceptor)
    api(libs.json)

    testImplementation(libs.okhttp.mockwebserver)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.jupiter.params)
    testImplementation(libs.assertj.vavr)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "http-client-okhttp"
            from(components["java"])
        }
    }
}

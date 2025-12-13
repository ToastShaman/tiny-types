dependencies {
    api(libs.spring.boot.starter.test)
    api(libs.okhttp.mockwebserver)

    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.jupiter.params)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "testing-http-client-okhttp-spring"
            from(components["java"])
        }
    }
}

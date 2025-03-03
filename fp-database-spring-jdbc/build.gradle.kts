dependencies {
    api(libs.vavr)
    api(libs.spring.jdbc)

    testImplementation(libs.h2)
    testImplementation(libs.flyway)
    testImplementation(libs.hikari)
    testImplementation(libs.assertj.core)
    testImplementation(libs.slf4j.simple)

    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.jupiter.params)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "fp-database-spring-jdbc"
            from(components["java"])
        }
    }
}
dependencies {
    api(project(":fp"))
    api(libs.vavr)
    api(libs.spring.jdbc)

    testImplementation(libs.h2)
    testImplementation(libs.flyway)
    testImplementation(libs.assertj.core)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.jupiter.params)
    testImplementation(libs.slf4j.simple)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "fp-database-spring-jdbc"
            from(components["java"])
        }
    }
}
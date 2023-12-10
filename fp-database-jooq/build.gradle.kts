dependencies {
    api(libs.vavr)
    api(libs.jooq)

    testImplementation(libs.h2)
    testImplementation(libs.flyway)
    testImplementation(libs.hikari)
    testImplementation(libs.assertj.core)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.jupiter.params)
    testImplementation(libs.slf4j.simple)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "fp-database-jooq"
            from(components["java"])
        }
    }
}
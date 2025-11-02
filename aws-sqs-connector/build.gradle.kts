dependencies {
    api(project(":core"))
    api(project(":fp"))
    api(project(":events"))
    api(libs.vavr)

    annotationProcessor(libs.record.builder.processor)
    implementation(libs.record.builder.core)

    implementation(libs.aws.sqs)
    implementation(libs.failsafe)

    testImplementation(libs.slf4j.api)
    testImplementation(libs.slf4j.simple)
    testImplementation(libs.json)
    testImplementation(libs.datafaker)
    testImplementation(libs.awaitility)

    testImplementation(libs.testcontainers.junit)
    testImplementation(libs.testcontainers.localstack)

    testImplementation(libs.assertj.core)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.jupiter.params)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "aws-sqs-connector"
            from(components["java"])
        }
    }
}

dependencies {
    api(project(":core"))
    api(project(":fp"))
    api(project(":events"))
    api(libs.vavr)

    annotationProcessor(libs.record.builder.processor)
    implementation(libs.record.builder.core)

    implementation(libs.aws.sqs)
    implementation(libs.aws.sns)
    implementation(libs.failsafe)
    implementation(libs.jackson.databind)
    implementation(libs.micrometer.core)
    implementation(libs.micrometer.tracing)
    implementation(libs.opentelemetry.sdk)
    implementation(libs.slf4j.api)

    testImplementation(libs.slf4j.jdk14)
    testImplementation(libs.json)
    testImplementation(libs.datafaker)
    testImplementation(libs.awaitility)

    testImplementation(libs.micrometer.tracing.testing)

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

plugins {
    kotlin("jvm")
}

kotlin {
    explicitApi()
}

tasks.test {
    useJUnitPlatform()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.6.10")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1")
    implementation("io.ktor:ktor-network:2.0.1")
    implementation("io.github.microutils:kotlin-logging:2.1.21")
    implementation("io.arrow-kt:arrow-core:1.1.2")

    // tests
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.1")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testImplementation("redis.clients:jedis:4.2.3")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
    testRuntimeOnly("org.slf4j:slf4j-simple:1.7.36")
}

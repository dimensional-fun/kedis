plugins {
    kotlin("multiplatform")
}

kotlin {
    explicitApi()

    jvm()

    linuxX64()

    sourceSets["commonMain"].dependencies {
        implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.0")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

        implementation("io.ktor:ktor-network:2.2.2") // raw sockets
        implementation("io.ktor:ktor-http:2.2.2")    // url class

        implementation("naibu.stdlib:naibu-core:1.0-RC.8")

        implementation("io.github.microutils:kotlin-logging:3.0.4")
    }

    sourceSets["commonTest"].dependencies {
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
    }

    sourceSets["jvmTest"].dependencies {
        implementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
        implementation("redis.clients:jedis:4.3.1")

        runtimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
        runtimeOnly("org.slf4j:slf4j-simple:2.0.5")
    }
}

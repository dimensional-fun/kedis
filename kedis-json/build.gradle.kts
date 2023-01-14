plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization") version "1.8.0"
}

kotlin {
    explicitApi()

    jvm()

    linuxX64()

    sourceSets["commonMain"].dependencies {
        implementation(project(":kedis-core"))
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
    }
}

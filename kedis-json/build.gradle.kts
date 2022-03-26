plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "1.6.10"
}

kotlin {
    explicitApi()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")

    implementation(project(":kedis-core"))
}

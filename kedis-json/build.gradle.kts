plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "1.6.10"
}

kotlin {
    explicitApi()
}

dependencies {
    implementation(project(":kedis-core"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")
}

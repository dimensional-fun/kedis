import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    idea
    `maven-publish`

    kotlin("multiplatform") version "1.8.0" apply false
}

allprojects {
    repositories {
        maven("https://maven.dimensional.fun/releases")
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "maven-publish")

    tasks {
        withType<KotlinCompile> {

            kotlinOptions {
                jvmTarget = "16"
                freeCompilerArgs = listOf(
                    "-Xopt-in=kotlin.contracts.ExperimentalContracts",
                    "-Xopt-in=kotlin.RequiresOptIn"
                )
            }
        }

        withType<JavaCompile> {
            sourceCompatibility = "16"
            targetCompatibility = "16"
        }
    }
}

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    idea
    `maven-publish`

    kotlin("jvm") version "1.6.10" apply false
}

subprojects {
    repositories {
        mavenCentral()
    }

    tasks {
        withType<KotlinCompile> {
            kotlinOptions {
                jvmTarget = "11"
                freeCompilerArgs = listOf(
                    "-Xopt-in=kotlin.contracts.ExperimentalContracts"
                )
            }
        }

        withType<JavaCompile> {
            sourceCompatibility = "11"
            targetCompatibility = "11"
        }
    }
}

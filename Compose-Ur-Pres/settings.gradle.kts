plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "Compose-Ur-Pres"

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        google()
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(
    ":cup",
    ":cup-gradle-plugin",
    ":cup-source-code",
    ":plugin:cup-laser",
    ":plugin:cup-speaker-window",
    ":widgets:cup-widgets-foundation",
    ":widgets:cup-widgets-material",
    ":widgets:cup-widgets-material3",
)

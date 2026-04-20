plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
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
    ":widgets:cup-widgets-source-code",
    ":plugin:cup-plugin-image-export",
    ":plugin:cup-plugin-key-events",
    ":plugin:cup-plugin-laser",
    ":plugin:cup-plugin-speaker-window",
    ":plugin:cup-plugin-auto-move",
    ":widgets:cup-widgets-foundation",
    ":widgets:cup-widgets-material2",
    ":widgets:cup-widgets-material3",
)

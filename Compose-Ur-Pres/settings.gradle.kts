plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

rootProject.name = "Compose-Ur-Pres"

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositories {
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
    ":widgets:cup-widget-material",
    ":widgets:cup-widget-material3",
)

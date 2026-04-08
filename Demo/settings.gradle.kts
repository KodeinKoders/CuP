rootProject.name = "CuP-Demo"

includeBuild("../Compose-Ur-Pres")

pluginManagement {
    includeBuild("../Compose-Ur-Pres")

    repositories {
        mavenLocal()
        gradlePluginPortal()
        google()
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        google()
    }
}
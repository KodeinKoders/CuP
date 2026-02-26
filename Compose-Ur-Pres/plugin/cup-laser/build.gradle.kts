import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose)
    alias(libs.plugins.kotlin.plugin.compose)
    alias(libs.plugins.dokka)
    alias(libs.plugins.mavenPublish)
}

kotlin {
    jvm()
    jvmToolchain(17)

    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }

    explicitApi()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.jetbrainsComposeRuntime)
            implementation(libs.jetbrainsComposeFoundation)
            implementation(libs.jetbrainsComposeMaterial)
            implementation(libs.jetbrainsComposeMaterialIcons)

            implementation(projects.cup)

            implementation(libs.kotlinx.collectionsImmutable)
        }

        all {
            languageSettings.optIn("net.kodein.cup.PluginCupAPI")
        }
    }
}

mavenPublishing {
    pom {
        name.set("CuP Laser plugin")
        description.set("A CuP plugin that allows to draw over the presentation.")
    }
}

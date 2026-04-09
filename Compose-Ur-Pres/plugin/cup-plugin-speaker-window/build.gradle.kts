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
    jvmToolchain(21)

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }

    explicitApi()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.material.icons)

            implementation(projects.cup)
            implementation(projects.plugin.cupPluginLaser)

            implementation(libs.kotlinx.collectionsImmutable)
        }

        jvmMain.dependencies {
            implementation(libs.markdown)
        }

        all {
            languageSettings.optIn("net.kodein.cup.PluginCupAPI")
        }
    }
}

mavenPublishing {
    pom {
        name.set("CuP Speaker Window plugin")
        description.set("A CuP plugin that allows the presenter to control their presentation from a dedicated window.")
    }
}

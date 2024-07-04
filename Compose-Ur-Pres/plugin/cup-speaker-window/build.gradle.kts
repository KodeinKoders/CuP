import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

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

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }

    explicitApi()

    sourceSets {
        commonMain.dependencies {
            implementation(kotlin.compose.runtime)
            implementation(kotlin.compose.foundation)
            implementation(kotlin.compose.material)
            implementation(kotlin.compose.materialIconsExtended)

            implementation(projects.cup)
            implementation(projects.plugin.cupLaser)

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

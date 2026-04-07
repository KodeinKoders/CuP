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

            implementation(libs.kotlinx.collectionsImmutable)
        }

        jvmMain.dependencies {
            implementation(libs.graalvm.js)
        }

        named("wasmJsMain").dependencies {
            implementation(npm("highlight.js", libs.versions.npm.highlightjs.get()))
        }

        all {
            languageSettings.optIn("net.kodein.cup.PluginCupAPI")
            languageSettings.optIn("net.kodein.cup.InternalCupAPI")
        }
    }
}

mavenPublishing {
    pom {
        name.set("CuP Source code composables")
        description.set("Provides static & animated source code composables for CuP.")
    }
}

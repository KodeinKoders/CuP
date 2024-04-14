import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose)
    `maven-publish`
}

kotlin {
    jvm()

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

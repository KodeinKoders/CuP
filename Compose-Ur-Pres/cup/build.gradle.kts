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

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.materialIconsExtended)

            api(libs.emojiCompose)
        }

        named("wasmJsMain").dependencies {
            implementation(npm("ismobilejs", libs.versions.npm.ismobilejs.get()))
        }

        explicitApi()

        all {
            languageSettings.optIn("net.kodein.cup.PluginCupAPI")
            languageSettings.optIn("net.kodein.cup.InternalCupAPI")
        }
    }
}

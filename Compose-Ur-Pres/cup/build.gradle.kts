import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose)
    alias(libs.plugins.dokka)
    alias(libs.plugins.mavenPublish)
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

mavenPublishing {
    pom {
        name.set("Compose ur Pres")
        description.set("A framework to program a presentation slide deck with Compose multiplatform.")
    }
}

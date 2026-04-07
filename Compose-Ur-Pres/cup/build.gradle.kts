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

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.material.icons)

            api(libs.emoji.compose)

            implementation(libs.kotlinx.collectionsImmutable)
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

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
            implementation(projects.cup)
            api(projects.widgets.cupWidgetsFoundation)
        }

        explicitApi()
    }
}

mavenPublishing {
    pom {
        name.set("Compose ur Pres UI widgets with Material 3")
        description.set("A subset of Composable UI components using Material Design 3.")
    }
}

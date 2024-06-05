package net.kodein.cup.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.*
import org.jetbrains.compose.ComposeExtension
import org.jetbrains.compose.ComposePlugin
import org.jetbrains.compose.desktop.DesktopExtension
import org.jetbrains.compose.experimental.dsl.ExperimentalExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl


public class CupPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = target.applyPlugin()

    private fun Project.applyPlugin() {
        val kotlin = extensions.findByName("kotlin")
            ?: error("Please apply the kotlin(\"multiplatform\") plugin before applying the CuP plugin")
        if (kotlin !is KotlinMultiplatformExtension) error("CuP only works with Kotlin Multiplatform (you have applied another Kotlin plugin).")

        val compose = extensions.findByName("compose")
            ?: error("Please apply the id(\"org.jetbrains.compose\") plugin before applying the CuP plugin")
        if (compose !is ComposeExtension) error("invalid compose extension")

        val composeDeps = (kotlin as ExtensionAware).extensions.findByName("compose")
            ?: error("Please apply the id(\"org.jetbrains.compose\") plugin before applying the CuP plugin")
        if (composeDeps !is ComposePlugin.Dependencies) error("invalid kotlin.compose extension")

        repositories {
            mavenCentral()
            google()
        }

        extensions.create<CupExtension>("cup", this, kotlin, compose, composeDeps)
        kotlin.extensions.create<CupDependencies>("cup")

        kotlin.apply {
            sourceSets.apply {
                commonMain.dependencies {
                    implementation(composeDeps.runtime)
                    implementation(composeDeps.foundation)
                    implementation(composeDeps.components.resources)

                    implementation("net.kodein.cup:cup:${CupBuildInfo.VERSION}")
                }
            }
        }
    }
}

package net.kodein.cup.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.create
import org.jetbrains.compose.ComposeExtension
import org.jetbrains.compose.ComposePlugin
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension


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

        extensions.create<CupExtension>("cup", this, kotlin, compose, composeDeps)
    }
}

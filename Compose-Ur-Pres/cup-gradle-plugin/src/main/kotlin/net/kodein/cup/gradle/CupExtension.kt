package net.kodein.cup.gradle

import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.*
import org.gradle.language.jvm.tasks.ProcessResources
import org.jetbrains.compose.ComposeExtension
import org.jetbrains.compose.ComposePlugin
import org.jetbrains.compose.desktop.DesktopExtension
import org.jetbrains.compose.experimental.dsl.ExperimentalExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

public abstract class CupExtension internal constructor(
    private val project: Project,
    private val kotlin: KotlinMultiplatformExtension,
    private val compose: ComposeExtension,
    private val composeDeps: ComposePlugin.Dependencies
) : ExtensionAware {
    public fun targetDesktop(mainClass: String = "MainKt") {
        kotlin.jvm()
        kotlin.apply {
            sourceSets.jvmMain.dependencies {
                implementation(composeDeps.desktop.currentOs)
            }
        }

        val extractIcons = project.tasks.register<ExtractResources>("extractIcons") {
            inputPaths.set(listOf("icons/cup.icns", "icons/cup.ico", "icons/cup.png"))
            outputDirectory.set(project.layout.buildDirectory.dir("cup"))
        }

        project.afterEvaluate {
            listOf("run", "createDistributable", "createReleaseDistributable").forEach { taskName ->
                project.tasks.named(taskName) {
                    dependsOn(extractIcons)
                }
            }
        }

        compose.extensions.configure<DesktopExtension>("desktop") {
            application.mainClass = mainClass
            application.nativeDistributions.includeAllModules = true

            application.nativeDistributions {
                macOS.iconFile.set { extractIcons.get().outputDirectory.get().file("icons/cup.icns").asFile }
                linux.iconFile.set { extractIcons.get().outputDirectory.get().file("icons/cup.png").asFile }
                windows.iconFile.set { extractIcons.get().outputDirectory.get().file("icons/cup.ico").asFile }
            }
        }
    }

    @OptIn(ExperimentalWasmDsl::class)
    public fun targetWeb() {
        kotlin.wasmJs {
            browser {
                runTask { mainOutputFileName.set("presentation.js") }
                webpackTask { mainOutputFileName.set("presentation.js") }
            }
            binaries.executable()
        }
        compose.extensions.getByName<ExperimentalExtension>("experimental").web.application {}

        val extractIndexHtml = project.tasks.register<ExtractResources>("extractIndexHtml") {
            inputPaths.set(listOf("html/index.html", "html/icon.png"))
            outputDirectory.set(project.layout.buildDirectory.dir("cup"))
        }
        project.tasks.named<ProcessResources>("wasmJsProcessResources") {
            dependsOn(extractIndexHtml)
            from(extractIndexHtml.get().outputDirectory.dir("html"))
        }
    }
}

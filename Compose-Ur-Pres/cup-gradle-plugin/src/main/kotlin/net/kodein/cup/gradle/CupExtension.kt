package net.kodein.cup.gradle

import org.gradle.api.Project
import org.gradle.api.tasks.Sync
import org.gradle.kotlin.dsl.*
import org.gradle.language.jvm.tasks.ProcessResources
import org.jetbrains.compose.ComposeExtension
import org.jetbrains.compose.ComposePlugin
import org.jetbrains.compose.desktop.DesktopExtension
import org.jetbrains.compose.experimental.dsl.ExperimentalExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.utils.provider

public class CupExtension internal constructor(
    private val project: Project,
    private val kotlin: KotlinMultiplatformExtension,
    private val compose: ComposeExtension,
    private val composeDeps: ComposePlugin.Dependencies
) {
    public fun targetDesktop(mainClass: String = "MainKt") {
        kotlin.jvm()
        kotlin.apply {
            sourceSets.jvmMain.dependencies {
                implementation(composeDeps.desktop.currentOs)
            }
        }
        compose.extensions.configure<DesktopExtension>("desktop") {
            application.mainClass = mainClass
            application.nativeDistributions.includeAllModules = true
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

        val extractIndexHtml = project.tasks.register<ExtractIndexHtml>("extractIndexHtml")
        project.tasks.named<ProcessResources>("wasmJsProcessResources") {
            dependsOn(extractIndexHtml)
            from(extractIndexHtml.get().output)
        }

        project.tasks.register<Sync>("distDoc") {
            group = "publishing"
            dependsOn("wasmJsBrowserDistribution")
            from("build/dist/wasmJs/productionExecutable")
            into("doc")
        }
    }
}

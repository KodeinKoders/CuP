package net.kodein.cup.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction


internal open class ExtractIndexHtml : DefaultTask() {

    @get:OutputFile
    internal val output = project.layout.buildDirectory.file("cup/html/index.html")

    @TaskAction
    private fun execute() {
        val outputFile = output.get().asFile
        outputFile.parentFile.mkdirs()
        val html = ExtractIndexHtml::class.java.getResourceAsStream("/index.html").use { it!!.reader().readText() }
        outputFile.writeText(html)
    }

}

package net.kodein.cup.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction


@CacheableTask
public abstract class ExtractResources : DefaultTask() {

    @get:Input
    public abstract val inputPaths: ListProperty<String>

    @get:OutputDirectory
    public abstract val outputDirectory: DirectoryProperty

    @TaskAction
    internal fun execute() {
        val outputDirectory = outputDirectory.get().asFile

        inputPaths.get().forEach { inputPath ->
            val outputFile = outputDirectory.resolve(inputPath)
            outputFile.parentFile.mkdirs()
            outputFile.outputStream().use { output ->
                ExtractResources::class.java.getResourceAsStream("/${inputPath}")!!.use { input ->
                    input.transferTo(output)
                }
            }
        }
    }

}

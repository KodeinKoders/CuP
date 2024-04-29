plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.compose) apply false
    alias(libs.plugins.dokka) apply false
    alias(libs.plugins.mavenPublish) apply false
}

allprojects {
    group = "net.kodein.cup"
    version = "1.0.0-Beta-01"

    /*
     In a composite build, tasks run from the root will not automatically propagate to subprojects (see
     https://github.com/gradle/gradle/issues/20863).
     This plugin is for root build scripts that do not themselves have a "publish" task. It adds a
     "publish" task that depends on the "publish" tasks of all subprojects, to emulate typical Gradle
     behavior.
    */
    apply { plugin("org.gradle.lifecycle-base") }
    afterEvaluate {
        listOf(
            "publishToMavenLocal" to "publishing",
            "publishToMavenCentral" to "release",
            "publishAndReleaseToMavenCentral" to "release",
        ).forEach { (taskName, taskGroup) ->
            if (taskName !in project.tasks.names) {
                project.tasks.register(taskName) {
                    group = taskGroup
                    dependsOn(subprojects.map { ":${it.path}:$taskName" })
                }
            }
        }

        listOf("build", "clean", "assemble").forEach { taskName ->
            tasks.named(taskName) {
                dependsOn(subprojects.map { ":${it.path}:$taskName" })
            }
        }
    }
}

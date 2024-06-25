plugins {
    `kotlin-dsl`
    alias(libs.plugins.buildConfig)
    alias(libs.plugins.dokka)
    alias(libs.plugins.mavenPublish)
    alias(libs.plugins.gradle.pluginPublish)
}

dependencies {
    compileOnly(gradleApi())
    compileOnly(
        group = libs.plugins.compose.get().pluginId,
        name = libs.plugins.compose.get().pluginId + ".gradle.plugin",
        version = libs.versions.compose.get()
    )
    compileOnly(
        group = libs.plugins.kotlin.multiplatform.get().pluginId,
        name = libs.plugins.kotlin.multiplatform.get().pluginId + ".gradle.plugin",
        version = libs.versions.kotlin.get()
    )
}

kotlin {
    explicitApi()
    jvmToolchain(17)
}

buildConfig {
    packageName("${project.group}.gradle")
    className("CupBuildInfo")
    buildConfigField("String", "VERSION", "\"${project.version}\"")
    useKotlinOutput {
        internalVisibility = true
    }
}

@Suppress("UnstableApiUsage")
gradlePlugin {
    website.set("https://kodeinkoders.github.io/CuP")
    vcsUrl.set("https://github.com/KodeinKoders/CuP")
    plugins.register("cup") {
        id = project.group.toString()
        implementationClass = "${project.group}.gradle.CupPlugin"
        displayName = "Compose ur Pres"
        description = "Configures a Kotlin/Multiplatform project for a Compose-ur-Pres presentation."
        @Suppress("UnstableApiUsage")
        tags.set(listOf("kotlin", "presentation"))
    }
}

System.getenv("GRADLE_PUBLISH_KEY")?.let { extra["gradle.publish.key"] = it }
System.getenv("GRADLE_PUBLISH_SECRET")?.let { extra["gradle.publish.secret"] = it }

mavenPublishing {
    pom {
        name.set("CuP Gradle plugin")
        description.set("A Gradle plugin that configures a project for CuP.")
    }
}

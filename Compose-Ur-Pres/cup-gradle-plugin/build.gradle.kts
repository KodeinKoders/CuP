plugins {
    `kotlin-dsl`
    alias(libs.plugins.buildConfig)
    alias(libs.plugins.dokka)
    alias(libs.plugins.mavenPublish)
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
}

buildConfig {
    packageName("${project.group}.gradle")
    className("CupBuildInfo")
    buildConfigField("String", "VERSION", "\"${project.version}\"")
    useKotlinOutput {
        internalVisibility = true
    }
}

gradlePlugin {
    plugins.register("cup") {
        id = project.group.toString()
        implementationClass = "${project.group}.gradle.CupPlugin"
        displayName = "CuP"
        description = "Compose ur Pres"
        @Suppress("UnstableApiUsage")
        tags.set(listOf("kotlin", "presentation"))
    }
}

System.getenv("GRADLE_PUBLISH_KEY")?.let { extra["gradle.publish.key"] = it }
System.getenv("GRADLE_PUBLISH_SECRET")?.let { extra["gradle.publish.secret"] = it }

@Suppress("UnstableApiUsage")
gradlePlugin {
    website.set("https://github.com/KodeinKoders/CuP")
    vcsUrl.set("https://github.com/KodeinKoders/CuP.git")
}

mavenPublishing {
    pom {
        name.set("CuP Gradle plugin")
        description.set("A Gradle plugin that configures a project for CuP.")
    }
}

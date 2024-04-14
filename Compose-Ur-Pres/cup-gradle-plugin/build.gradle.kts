plugins {
    `kotlin-dsl`
    alias(libs.plugins.buildConfig)
    `maven-publish`
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
        description = "Compose Ur Pres"
        @Suppress("UnstableApiUsage")
        tags.set(listOf("kotlin", "presentation"))
    }
}

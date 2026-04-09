plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose)
    alias(libs.plugins.kotlin.plugin.compose)
    alias(libs.plugins.cup)
}

cup {
    targetDesktop()
    targetWeb()
}

kotlin {
    sourceSets.commonMain.dependencies {
        implementation(libs.bundles.compose)
        implementation(libs.bundles.cup)
        implementation(libs.emoji.compose)
    }
}

plugins {
    kotlin("multiplatform") version "2.0.20"
    id("org.jetbrains.compose") version "1.6.11"
    kotlin("plugin.compose") version "2.0.20"
    id("net.kodein.cup")
}

cup {
    targetDesktop()
    targetWeb()
}

repositories {
    mavenLocal()
}

kotlin {
    sourceSets.commonMain.dependencies {
        implementation(cup.sourceCode)
        implementation(cup.plugin.laser)
        implementation(cup.plugin.speakerWindow)

        implementation(compose.material)
        implementation(compose.materialIconsExtended)
        implementation(cup.widgets.material)
    }
}

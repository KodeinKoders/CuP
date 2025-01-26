plugins {
    kotlin("multiplatform") version "2.1.0"
    id("org.jetbrains.compose") version "1.7.3"
    kotlin("plugin.compose") version "2.1.0"
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

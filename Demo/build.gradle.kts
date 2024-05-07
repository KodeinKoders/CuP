plugins {
    kotlin("multiplatform") version "1.9.23"
    id("org.jetbrains.compose") version "1.6.1"
    id("net.kodein.cup")
}

cup {
    targetDesktop()
    targetWeb()
}

kotlin {
    sourceSets.commonMain.dependencies {
        implementation(cup.sourceCode)
        implementation(cup.plugin.laser)
        implementation(cup.plugin.speakerWindow)
        implementation(cup.widget.material)
        implementation(cup.widget.material3)


        implementation(compose.material)
        implementation(compose.material3)
        implementation(compose.materialIconsExtended)
    }
}

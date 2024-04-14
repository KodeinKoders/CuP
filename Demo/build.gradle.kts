plugins {
    kotlin("multiplatform") version "1.9.23"
    id("org.jetbrains.compose") version "1.6.1"
    id("net.kodein.cup") version "1.0.0-Beta-01"
}

cup {
    targetDesktop()
    targetWeb()
}

kotlin {
    sourceSets.commonMain.dependencies {
        implementation(cup.sourceCode)
        implementation(cup.plugins.laser)
        implementation(cup.plugins.speakerWindow)

        implementation(compose.material)
        implementation(compose.materialIconsExtended)
    }
}

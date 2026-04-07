plugins {
    kotlin("multiplatform") version "2.3.10"
    id("org.jetbrains.compose") version "1.10.1"
    kotlin("plugin.compose") version "2.3.10"
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
        implementation(cup.plugin.imageExport)
        implementation(cup.plugin.laser)
        implementation(cup.plugin.speakerWindow)

        implementation("org.jetbrains.compose.material3:material3:1.10.0-alpha05")
        implementation("org.jetbrains.compose.material:material-icons-extended:1.7.3")
        implementation(cup.widgets.material3)
    }
}



plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.compose) apply false
}

allprojects {
    group = "net.kodein.cup"
    version = "1.0.0-Beta-01"
}

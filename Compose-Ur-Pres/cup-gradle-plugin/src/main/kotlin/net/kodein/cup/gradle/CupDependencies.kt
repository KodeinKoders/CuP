package net.kodein.cup.gradle

import org.gradle.api.plugins.ExtensionAware


public abstract class CupDependencies internal constructor() : ExtensionAware {
    public val sourceCode: String = "net.kodein.cup:cup-source-code:${CupBuildInfo.VERSION}"

    public val plugin: Plugins = Plugins()
    public class Plugins internal constructor() {
        public val laser: String = "net.kodein.cup:cup-laser:${CupBuildInfo.VERSION}"
        public val speakerWindow: String = "net.kodein.cup:cup-speaker-window:${CupBuildInfo.VERSION}"
    }

    public val widgets: Widgets = Widgets()
    public class Widgets internal constructor() {
        public val material: String = "net.kodein.cup:cup-widgets-material:${CupBuildInfo.VERSION}"
        public val material3: String = "net.kodein.cup:cup-widgets-material3:${CupBuildInfo.VERSION}"
    }
}

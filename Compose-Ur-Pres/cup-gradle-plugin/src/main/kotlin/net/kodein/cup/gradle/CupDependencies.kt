package net.kodein.cup.gradle


public class CupDependencies internal constructor() {
    public val sourceCode: String = "net.kodein.cup:cup-source-code:${CupBuildInfo.VERSION}"

    public val plugin: Plugins = Plugins()
    public class Plugins internal constructor() {
        public val laser: String = "net.kodein.cup:cup-laser:${CupBuildInfo.VERSION}"
        public val speakerWindow: String = "net.kodein.cup:cup-speaker-window:${CupBuildInfo.VERSION}"
    }

    public val widget: Widgets = Widgets()
    public class Widgets internal constructor() {
        public val material: String = "net.kodein.cup:cup-widget-material:${CupBuildInfo.VERSION}"
        public val material3: String = "net.kodein.cup:cup-widget-material3:${CupBuildInfo.VERSION}"
    }
}

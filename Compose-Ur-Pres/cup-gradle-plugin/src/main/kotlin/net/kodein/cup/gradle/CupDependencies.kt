package net.kodein.cup.gradle


public class CupDependencies internal constructor() {
    public val sourceCode: String = "net.kodein.cup:cup-source-code:${CupBuildInfo.VERSION}"

    public val plugin: Plugins = Plugins()
    public class Plugins internal constructor() {
        public val laser: String = "net.kodein.cup:cup-laser:${CupBuildInfo.VERSION}"
        public val speakerWindow: String = "net.kodein.cup:cup-speaker-window:${CupBuildInfo.VERSION}"
    }
}

package net.kodein.cup.config

import net.kodein.cup.SlideSpecs


@DslMarker
public annotation class CupConfigurationDsl


@CupConfigurationDsl
public class CupConfigurationBuilder internal constructor() {
    @PublishedApi
    internal val plugins: MutableList<CupPlugin> = mutableListOf()

    @CupConfigurationDsl
    public fun CupConfigurationBuilder.plugin(plugin: CupPlugin) {
        plugins.add(plugin)
    }

    public var defaultSlideSpecs: SlideSpecs? = null
}

package net.kodein.cup.utils

import androidx.compose.material.Colors
import androidx.compose.ui.graphics.Color
import net.kodein.cup.PluginCupAPI


@PluginCupAPI
public object CupToolsColors {
    public val orange: Color get() = Color(0xFF_E8441F)
    public val orange_dark: Color get() = Color(0xFF_A6301F)
    public val orange_light: Color get() = Color(0xFF_EC755B)

    public val purple: Color get() = Color(0xFF_921F81)
    public val purple_dark: Color get() = Color(0xFF_6D1761)
    public val purple_light: Color get() = Color(0xFF_B35C9D)

    public val light: Color get() = Color(0xFF_F7E1DE)
    public val light_purple: Color get() = Color(0xFF_D39AB8)
    public val light_orange: Color get() = Color(0xFF_F0A698)
    public val lighter: Color get() = Color(0xFF_FBF0EE)

    public val dark: Color get() = Color(0xFF_240821)
    public val dark_orange: Color get() = Color(0xFF_651B20)
    public val dark_purple: Color get() = Color(0xFF_480F40)
    public val darker: Color get() = Color(0xFF_120411)

}

@PluginCupAPI
public val CupToolsMaterialColors: Colors = Colors(
    primary = CupToolsColors.dark_purple,
    primaryVariant =  CupToolsColors.purple_dark,
    secondary = CupToolsColors.orange_dark,
    secondaryVariant = CupToolsColors.dark_orange,
    background = CupToolsColors.lighter,
    surface = CupToolsColors.lighter,
    error = CupToolsColors.dark_orange,
    onPrimary = CupToolsColors.light,
    onSecondary = CupToolsColors.light,
    onBackground = CupToolsColors.darker,
    onSurface = CupToolsColors.darker,
    onError = CupToolsColors.orange_light,
    isLight = true
)

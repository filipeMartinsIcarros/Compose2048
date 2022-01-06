package com.example.compose2048.theme.tokens

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val color2 = Color(0xff50c0e9)
private val color2Dark = Color(0xff4e6cef)
private val color4 = Color(0xff1da9da)
private val color4Dark = Color(0xff3f51b5)
private val color8 = Color(0xff8e24aa)
private val color8Dark = Color(0xff8e24aa)
private val color16 = Color(0xffb368d9)
private val color16Dark = Color(0xff673ab7)
private val color32 = Color(0xffff5f5f)
private val color32Dark = Color(0xffc00c23)
private val color64 = Color(0xffe92727)
private val color64Dark = Color(0xffa80716)
private val color128 = Color(0xff92c500)
private val color128Dark = Color(0xff0a7e07)
private val color256 = Color(0xff7caf00)
private val color256Dark = Color(0xff056f00)
private val color512 = Color(0xffffc641)
private val color512Dark = Color(0xffe37c00)
private val color1024 = Color(0xffffa713)
private val color1024Dark = Color(0xffd66c00)
private val color2048 = Color(0xffff8a00)
private val color2048Dark = Color(0xffcf5100)
private val color4096 = Color(0xffcc0000)
private val color4096Dark = Color(0xff80020a)
private val color8192 = Color(0xff0099cc)
private val color8192Dark = Color(0xff303f9f)
private val color16384 = Color(0xff9933cc)
private val color16384Dark = Color(0xff512da8)
private val colorEmptyTitle = Color(0xffdddddd)
private val colorEmptyTitleDark = Color(0xff444444)

class MainColor(
    val color2: Color,
    val color4: Color,
    val color8: Color,
    val color16: Color,
    val color32: Color,
    val color64: Color,
    val color128: Color,
    val color256: Color,
    val color512: Color,
    val color1024: Color,
    val color2048: Color,
    val color4096: Color,
    val color8192: Color,
    val color16384: Color,
    val colorEmptyTitle: Color
)

val lightColorPalette = MainColor(
    color2 = color2,
    color4 = color4,
    color8 = color8,
    color16 = color16,
    color32 = color32,
    color64 = color64,
    color128 = color128,
    color256 = color256,
    color512 = color512,
    color1024 = color1024,
    color2048 = color2048,
    color4096 = color4096,
    color8192 = color8192,
    color16384 = color16384,
    colorEmptyTitle = colorEmptyTitle
)

val darkColorPalette = MainColor(
    color2 = color2Dark,
    color4 = color4Dark,
    color8 = color8Dark,
    color16 = color16Dark,
    color32 = color32Dark,
    color64 = color64Dark,
    color128 = color128Dark,
    color256 = color256Dark,
    color512 = color512Dark,
    color1024 = color1024Dark,
    color2048 = color2048Dark,
    color4096 = color4096Dark,
    color8192 = color8192Dark,
    color16384 = color16384Dark,
    colorEmptyTitle = colorEmptyTitleDark
)

val LocalMainColorsProvider = staticCompositionLocalOf {
    lightColorPalette
}
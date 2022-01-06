package com.example.compose2048.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import com.example.compose2048.theme.tokens.LocalMainColorsProvider
import com.example.compose2048.theme.tokens.MainColor
import com.example.compose2048.theme.tokens.darkColorPalette
import com.example.compose2048.theme.tokens.lightColorPalette

@Composable
fun AppTheme(darkTheme: Boolean = isSystemInDarkTheme(),content: @Composable () -> Unit) {
    val colors = if (darkTheme) darkColorPalette else lightColorPalette

    CompositionLocalProvider(
        LocalMainColorsProvider provides colors
    ) {
        MaterialTheme(
            content = content
        )
    }
}

object AppTheme {
    val color: MainColor
        @Composable
        get() = LocalMainColorsProvider.current
}
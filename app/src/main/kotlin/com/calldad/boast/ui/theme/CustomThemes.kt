package com.calldad.boast.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

fun getCustomColorScheme(themeColor: String, isDark: Boolean): androidx.compose.material3.ColorScheme {
    return when (themeColor) {
        "purple" -> {
            if (isDark) {
                darkColorScheme(
                    primary = Color(0xFFBB86FC),
                    secondary = Color(0xFF03DAC6),
                    tertiary = Color(0xFFCF6679)
                )
            } else {
                lightColorScheme(
                    primary = Color(0xFF6200EE),
                    secondary = Color(0xFF03DAC6),
                    tertiary = Color(0xFF3700B3)
                )
            }
        }
        "blue" -> {
            if (isDark) {
                darkColorScheme(
                    primary = Color(0xFF90CAF9),
                    secondary = Color(0xFF81D4FA),
                    tertiary = Color(0xFF64B5F6)
                )
            } else {
                lightColorScheme(
                    primary = Color(0xFF2196F3),
                    secondary = Color(0xFF03A9F4),
                    tertiary = Color(0xFF1976D2)
                )
            }
        }
        "green" -> {
            if (isDark) {
                darkColorScheme(
                    primary = Color(0xFF81C784),
                    secondary = Color(0xFFA5D6A7),
                    tertiary = Color(0xFF66BB6A)
                )
            } else {
                lightColorScheme(
                    primary = Color(0xFF4CAF50),
                    secondary = Color(0xFF8BC34A),
                    tertiary = Color(0xFF388E3C)
                )
            }
        }
        "pink" -> {
            if (isDark) {
                darkColorScheme(
                    primary = Color(0xFFF48FB1),
                    secondary = Color(0xFFF06292),
                    tertiary = Color(0xFFE91E63)
                )
            } else {
                lightColorScheme(
                    primary = Color(0xFFE91E63),
                    secondary = Color(0xFFFF5722),
                    tertiary = Color(0xFFC2185B)
                )
            }
        }
        "orange" -> {
            if (isDark) {
                darkColorScheme(
                    primary = Color(0xFFFFB74D),
                    secondary = Color(0xFFFFA726),
                    tertiary = Color(0xFFFF9800)
                )
            } else {
                lightColorScheme(
                    primary = Color(0xFFFF5722),
                    secondary = Color(0xFFFF9800),
                    tertiary = Color(0xFFF57C00)
                )
            }
        }
        else -> {
            if (isDark) {
                darkColorScheme(
                    primary = Green80,
                    secondary = GreenGrey80,
                    tertiary = LightGreen80
                )
            } else {
                lightColorScheme(
                    primary = Green40,
                    secondary = GreenGrey40,
                    tertiary = LightGreen40
                )
            }
        }
    }
}
package com.calldad.boast.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.calldad.boast.viewmodel.SettingsViewModel

val DarkColorScheme = darkColorScheme(
    primary = Green80,
    secondary = GreenGrey80,
    tertiary = LightGreen80
)

val LightColorScheme = lightColorScheme(
    primary = Green40,
    secondary = GreenGrey40,
    tertiary = LightGreen40
)

@Composable
fun ComposeEmptyActivityTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun ComposeEmptyActivityTheme(
    settingsViewModel: SettingsViewModel,
    content: @Composable () -> Unit
) {
    val themeMode by settingsViewModel.themeMode.collectAsState()
    val themeColor by settingsViewModel.themeColor.collectAsState()
    val systemDarkTheme = isSystemInDarkTheme()
    
    val isDark = when (themeMode) {
        "dark" -> true
        "light" -> false
        else -> systemDarkTheme
    }
    
    val useDynamic = themeMode == "default" && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    
    val colorScheme = when {
        useDynamic && !isDark -> {
            dynamicLightColorScheme(LocalContext.current)
        }
        useDynamic && isDark -> {
            dynamicDarkColorScheme(LocalContext.current)
        }
        themeColor == "default" && isDark -> DarkColorScheme
        themeColor == "default" && !isDark -> LightColorScheme
        else -> getCustomColorScheme(themeColor, isDark)
    }
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
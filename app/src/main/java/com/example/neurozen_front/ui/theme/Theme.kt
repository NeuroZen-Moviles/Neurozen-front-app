package com.example.neurozen_front.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Forest700,
    onPrimary = White,
    secondary = Emerald600,
    onSecondary = White,
    tertiary = Sage500,
    onTertiary = Forest900,
    background = Mint100,
    onBackground = Forest900,
    surface = White,
    onSurface = Forest900,
    surfaceVariant = Sand100,
    outline = Sage500
)

private val LightColorScheme = lightColorScheme(
    primary = Forest700,
    onPrimary = White,
    secondary = Emerald600,
    onSecondary = White,
    tertiary = Sage500,
    onTertiary = Forest900,
    background = Mint100,
    onBackground = Forest900,
    surface = White,
    onSurface = Forest900,
    surfaceVariant = Sand100,
    outline = Sage500
)
@Composable
fun NeurozenTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
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

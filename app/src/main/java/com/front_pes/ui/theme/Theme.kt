package com.front_pes.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

val LightGray = Color(0xFFC4C4C4)
val DarkGray = Color(0xFF343434)
val LightSelectedGray = Color(0xFFB0B0B0)
val DarkSelectedGray = Color(0xFF5D5D5D)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF6DFFCD),
    secondary = Color.Black,
    tertiary = Color.Black,
    surface = Color(0xFF0C0C0C),
    surfaceVariant = Color(0xFF2C2C2C),
    onSurface = Color.White,
    onSurfaceVariant = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xBA22A908),
    secondary = Color.Black,
    tertiary = Color.Black,
    surface = Color.White,
    surfaceVariant = Color.White,
    onSurface = Color.Black,
    onSurfaceVariant = Color.Black
)

data class CustomColors(
    val bottomBar: Color,
    val selectedItem: Color
)

val LocalCustomColors = compositionLocalOf {
    CustomColors(
        bottomBar = Color.Unspecified,
        selectedItem = Color.Unspecified
    )
}

@Composable
fun FRONTPESTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
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

    val customColors = if (darkTheme) {
        CustomColors(
            bottomBar = DarkGray,
            selectedItem = DarkSelectedGray)
    } else {
        CustomColors(
            bottomBar = LightGray,
            selectedItem = LightSelectedGray
        )
    }

    CompositionLocalProvider(LocalCustomColors provides customColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
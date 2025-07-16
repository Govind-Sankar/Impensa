package com.nebulae.impensa.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
//    primary = Purple80,
    primary = Color(0xFF248BF3),
    secondary = PurpleGrey80,
    tertiary = Pink80,
    surface = Color(0xFF101A23),
    onSurface = Color(0xFFFFFFFF),
    background = Color(0xFF182634),
    onPrimary = Color(0xFF223649)
)

private val LightColorScheme = lightColorScheme(
//    primary = Purple40,
    primary = Color(0xFF248BF3),
    secondary = PurpleGrey40,
    tertiary = Pink40,
    surface = Color(0xFFF8FAFC),
    onSurface = Color(0xFF000000),
    background = Color(0xFFEEF5FA),
    onPrimary = Color(0xFFE7EDF4)
    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)


@Composable
fun ImpensaExpenseTrackerAppTheme(
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
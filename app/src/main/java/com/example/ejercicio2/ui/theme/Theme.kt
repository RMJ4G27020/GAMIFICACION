package com.example.ejercicio2.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat

// Esquema de colores oscuro mejorado para gamificación
private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlueLight,
    onPrimary = Color(0xFF002F65),
    primaryContainer = Color(0xFF004494),
    onPrimaryContainer = Color(0xFFD3E4FF),
    
    secondary = SecondaryGreen,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF00622B),
    onSecondaryContainer = Color(0xFFA8F4BA),
    
    tertiary = AccentOrange,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF7F2C00),
    onTertiaryContainer = Color(0xFFFFDBCA),
    
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    
    background = Color(0xFF121212),
    onBackground = Color(0xFFE4E2E6),
    
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE4E2E6),
    surfaceVariant = Color(0xFF2C2C2C),
    onSurfaceVariant = Color(0xFFC7C5CA),
    
    outline = Color(0xFF918F94),
    outlineVariant = Color(0xFF49454E),
    
    inverseSurface = Color(0xFFE4E2E6),
    inverseOnSurface = Color(0xFF2F3033),
    inversePrimary = Color(0xFF005CBC)
)

// Esquema de colores claro mejorado para gamificación
private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD3E4FF),
    onPrimaryContainer = Color(0xFF001B3D),
    
    secondary = SecondaryGreen,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFA8F4BA),
    onSecondaryContainer = Color(0xFF00210D),
    
    tertiary = AccentOrange,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFDBCA),
    onTertiaryContainer = Color(0xFF2E1500),
    
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    
    background = BackgroundLight,
    onBackground = Color(0xFF1A1C1E),
    
    surface = SurfaceLight,
    onSurface = Color(0xFF1A1C1E),
    surfaceVariant = Color(0xFFE2E1EC),
    onSurfaceVariant = Color(0xFF45464F),
    
    outline = Color(0xFF767680),
    outlineVariant = Color(0xFFC7C5D0),
    
    inverseSurface = Color(0xFF2F3033),
    inverseOnSurface = Color(0xFFF1F0F4),
    inversePrimary = PrimaryBlueLight
)

@Composable
fun Ejercicio2Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Deshabilitado por defecto para mantener consistencia de la marca
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
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Usar el nuevo API de WindowInsetsController
            WindowCompat.setDecorFitsSystemWindows(window, false)
            @Suppress("DEPRECATION")  // Necesario para compatibilidad con versiones antiguas
            window.statusBarColor = android.graphics.Color.TRANSPARENT
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
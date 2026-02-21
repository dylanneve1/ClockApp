package com.clockapp.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFBB86FC),
    onPrimary = Color(0xFF000000),
    primaryContainer = Color(0xFF3700B3),
    onPrimaryContainer = Color(0xFFE8DEF8),
    secondary = Color(0xFF03DAC6),
    onSecondary = Color(0xFF000000),
    secondaryContainer = Color(0xFF005047),
    onSecondaryContainer = Color(0xFF70F5E8),
    tertiary = Color(0xFFCF6679),
    onTertiary = Color(0xFF000000),
    tertiaryContainer = Color(0xFF8C1D35),
    onTertiaryContainer = Color(0xFFFFD9DF),
    background = Color(0xFF0D0D12),
    onBackground = Color(0xFFE6E1E5),
    surface = Color(0xFF0D0D12),
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF1D1D28),
    onSurfaceVariant = Color(0xFFCAC4D0),
    outline = Color(0xFF938F99),
    error = Color(0xFFCF6679),
    onError = Color(0xFF000000),
    errorContainer = Color(0xFF8C1D35),
    onErrorContainer = Color(0xFFFFD9DF)
)

@Composable
fun ClockAppTheme(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val dynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    
    val colorScheme = when {
        dynamicColor -> dynamicDarkColorScheme(context)
        else -> DarkColorScheme
    }
    
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}

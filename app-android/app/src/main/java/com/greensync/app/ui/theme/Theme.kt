package com.greensync.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColorScheme(
    primary = Color(0xFF0F9D58),
    secondary = Color(0xFF4285F4)
)

private val LightColors = lightColorScheme(
    primary = Color(0xFF0F9D58),
    secondary = Color(0xFF4285F4)
)

@Composable
fun GreenSyncTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = MaterialTheme.typography,
        content = content
    )
}

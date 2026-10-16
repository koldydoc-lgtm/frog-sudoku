package com.frogsudoku.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val SageGreen = Color(0xFFA3B899)
val DeepFrogGreen = Color(0xFF4A6B53)
val MintCream = Color(0xFFF0F7F4)
val SoftMoss = Color(0xFFCCD5AE)
val WarmBackground = Color(0xFFFAEDCD)
val PaperBackground = Color(0xFFF8F9FA)
val BlushPink = Color(0xFFF4A298)
val ErrorCoral = Color(0xFFE07A5F)
val TextDarkMoss = Color(0xFF2B3A2E)

private val FrogColorScheme = lightColorScheme(
    primary = DeepFrogGreen,
    onPrimary = Color.White,
    primaryContainer = SoftMoss,
    onPrimaryContainer = TextDarkMoss,
    secondary = SageGreen,
    background = WarmBackground,
    surface = MintCream,
    onBackground = TextDarkMoss,
    onSurface = TextDarkMoss,
    error = ErrorCoral
)

@Composable
fun FrogSudokuTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = FrogColorScheme, content = content)
}

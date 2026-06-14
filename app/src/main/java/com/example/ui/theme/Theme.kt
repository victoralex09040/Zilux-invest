package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
  primary = EmeraldGreen,
  onPrimary = DeepObsidian,
  secondary = ElectricBlue,
  onSecondary = Color.White,
  tertiary = GoldAccent,
  background = DeepObsidian,
  onBackground = Color.White,
  surface = DarkSlateCard,
  onSurface = Color.White,
  error = LossRed,
  onError = Color.White
)

private val LightColorScheme = DarkColorScheme // Force dark mode across the board for a professional investment terminal feel

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Force dark theme by default
  dynamicColor: Boolean = false, // Disable system pastels to keep the premium dark slate/green branding consistent
  content: @Composable () -> Unit,
) {
  val colorScheme = DarkColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

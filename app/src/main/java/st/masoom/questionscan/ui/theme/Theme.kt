package st.masoom.questionscan.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF2094F3), // Blue
    secondary = Color(0xFF3498DB), // Sky Blue
    background = Color.White, // White background
    onBackground = Color.Black, // Black text
    surface = Color.White, // Surface elements
    onSurface = Color.Black, // Text color on surface
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF2094F3), // Blue\
    secondary = Color(0xFF3498DB), // Sky Blue
    background = Color.Black, // Black background
    onBackground = Color.White, // White text
    surface = Color.Black, // Surface elements
    onSurface = Color.White, // Text color on surface
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(), // Auto-detect system theme
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colors,
        typography = Typography(),
        content = content
    )
}

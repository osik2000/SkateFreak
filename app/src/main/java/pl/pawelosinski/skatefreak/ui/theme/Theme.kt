package pl.pawelosinski.skatefreak.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat


private val myDarkColorScheme = darkColorScheme( // main and only scheme for now
    primary = MyPrimaryColor,
    secondary = MySecondaryColor,
    tertiary = MyTertiaryColor,
    background = MyBackgroundColor,
    surface = MySurfaceColor,
    onPrimary = MyWhiteColor,
    onSecondary = MyWhiteColor,
    onTertiary = MyWhiteColor,
    onBackground = MyWhiteColor,
    onSurface = MySecondaryColor
)

private val myLightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = PinkBG,
)

@Composable
fun SkateFreakTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) myDarkColorScheme else myLightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
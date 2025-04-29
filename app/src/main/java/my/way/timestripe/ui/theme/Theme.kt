package my.way.timestripe.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext



@Composable
fun TimestripeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    CompositionLocalProvider(
        LocalTimestripeColors provides colorScheme,
        LocalTimestripeTypography provides elkitapTypography
    ) {
        MaterialTheme(
            content = content
        )
    }
}

object TimestripeTheme {
    val colorScheme: TimestripeColors
        @Composable
        get() = LocalTimestripeColors.current

    val typography: TimestripeTypography
        @Composable
        get() = LocalTimestripeTypography.current
}
package theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object RedcodeTheme {
    val typography: Typography
        @Composable get() = MaterialTheme.typography.copy(
            subtitle2 = MaterialTheme.typography.subtitle2.copy(
                fontWeight = FontWeight.Bold
            ),
            body2 = MaterialTheme.typography.body2.copy(
                fontFamily = FontFamily.Monospace
            )
        )
    val Typography.body3
        @Composable get() = MaterialTheme.typography.body2.copy(
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp
        )
    val arrangement = Arrangement.spacedBy(16.dp)
}

@Composable
fun RedcodeTheme(
    content: @Composable () -> Unit
) = MaterialTheme(
    typography = RedcodeTheme.typography,
    content = content
)
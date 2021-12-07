import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import theme.LightGrayShade
import theme.RedcodeTheme

@Composable
fun Editor(
    text: String,
    onTextChange: (text: String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "CODE EDITOR",
            style = MaterialTheme.typography.subtitle2
        )
        TextField(
            value = text,
            onValueChange = onTextChange,
            shape = RectangleShape,
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.LightGrayShade,
                focusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            textStyle = RedcodeTheme.typography.body2,
            modifier = Modifier.fillMaxSize()
        )
    }
}

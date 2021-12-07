import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RedcodeLayout(modifier: Modifier = Modifier) {
    var sourceCode by remember { mutableStateOf(TestUtils.simpleProgram()) }
    Row(
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Editor(
            text = sourceCode,
            modifier = Modifier.weight(0.4f),
            onTextChange = { text -> sourceCode = text }
        )
        Interpreter(
            sourceCode = sourceCode,
            modifier = modifier.weight(1f)
        )
    }
}


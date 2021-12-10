import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import model.Command
import model.RedcodeProgram
import theme.LightGrayShade
import theme.RedcodeTheme
import utils.RedcodeParser

@Composable
@Preview
fun Interpreter(
    sourceCode: String,
    modifier: Modifier = Modifier
) {
    var program by remember { mutableStateOf(getRedcodeProgram(sourceCode)) }
    var execute by remember { mutableStateOf(false) }
    var jumpTo by remember { mutableStateOf(0) }
    val lazyListState = rememberLazyListState()
    LaunchedEffect(sourceCode) { program = getRedcodeProgram(sourceCode) }
    LaunchedEffect(jumpTo) { lazyListState.scrollToItem(jumpTo) }
    LaunchedEffect(execute) {
        launch(Dispatchers.Default) {
            while (program.isRunning && execute) {
                delay(5)
                program = program.step()
            }
        }
    }
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "INTERPRETER", style = MaterialTheme.typography.subtitle2)
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Column(modifier = Modifier.weight(1f).background(Color.LightGrayShade)) {
                TableHeaderRow()
                LazyColumn(state = lazyListState) {
                    item { Spacer(Modifier.height(16.dp)) }
                    itemsIndexed(program.commands) { index, item ->
                        TableCommandRow(index, item)
                    }
                }
            }
            Column(
                modifier = Modifier.weight(0.3f),
                content = {
                    ButtonsColumn(
                        jumpTo = jumpTo,
                        line = program.commands.indexOfFirst { command ->
                            command.isSelected
                        },
                        onSingleStepClick = {
                            execute = false
                            program = program.step()
                        },
                        onExecuteClick = {
                            execute = true
                        },
                        onStopClick = {
                            execute = false
                        },
                        onResetClick = {
                            execute = false
                            program = getRedcodeProgram(sourceCode)
                        },
                        onJumpToClick = { position ->
                            jumpTo = position
                        }
                    )
                }
            )
        }
    }
}

@Composable
fun TableHeaderRow(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
            TableTextCeil("LINE", true)
            TableTextCeil("COMMAND", true)
            TableTextCeil("ADDR A", true)
            TableTextCeil("ADDR B", true)
        }
        Divider(modifier = Modifier.fillMaxWidth())
    }
}

@Composable
fun TableCommandRow(
    index: Int,
    command: Command,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth().background(if (command.isSelected) Color.White else Color.LightGrayShade)
    ) {
        TableTextCeil(index.toString())
        TableTextCeil(command.name)
        TableTextCeil(command.operandA.toString())
        TableTextCeil(command.operandB.toString())
    }
}

@Composable
fun RowScope.TableTextCeil(
    text: String,
    isHeader: Boolean = false
) = Text(
    text = text,
    modifier = Modifier.weight(1f),
    textAlign = TextAlign.Center,
    style = if (isHeader) RedcodeTheme.typography.subtitle2 else RedcodeTheme.typography.body2,
    maxLines = 1,
    overflow = TextOverflow.Ellipsis
)

@Composable
private fun ButtonsColumn(
    jumpTo: Int,
    line: Int,
    onSingleStepClick: () -> Unit = {},
    onExecuteClick: () -> Unit = {},
    onStopClick: () -> Unit = {},
    onResetClick: () -> Unit = {},
    onJumpToClick: (position: Int) -> Unit = {}
) {
    var localJumpTo by remember { mutableStateOf(jumpTo.toString()) }
    ActionButton(text = "Run", onClick = onExecuteClick)
    ActionButton(text = "Stop", onClick = onStopClick)
    ActionButton(text = "Next", onClick = onSingleStepClick)
    ActionButton(text = "Reset", onClick = onResetClick)
    Spacer(Modifier.height(8.dp))
    TextField(
        value = localJumpTo,
        label = { Text("Jump to") },
        maxLines = 1,
        onValueChange = { text ->
            localJumpTo = text
            val value = text.filter(Char::isDigit).toIntOrNull()
            if (value != null) {
                onJumpToClick(value)
            }
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RectangleShape
    )
    Spacer(Modifier.height(8.dp))
    if (line >= 0) {
        Text(text = "Current line: $line", style = RedcodeTheme.typography.body2)
    }
}

@Composable
fun ActionButton(text: String, onClick: () -> Unit) {
    OutlinedButton(onClick, shape = RectangleShape, modifier = Modifier.fillMaxWidth()) {
        Text(text)
    }
}

fun getRedcodeProgram(
    sourceCode: String
): RedcodeProgram {
    return try {
        RedcodeParser.parse(sourceCode)
    } catch (e: Throwable) {
        RedcodeProgram.empty()
    }
}

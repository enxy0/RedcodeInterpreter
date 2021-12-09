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

private class InterpreterState(sourceCode: String) {
    var program by mutableStateOf(setSourceCode(sourceCode))
        private set
    var isExecuting by mutableStateOf(false)
    var scrollPosition by mutableStateOf(0)

    val currentLine by mutableStateOf(program.commands.indexOfFirst { command -> command.isSelected })

    fun setSourceCode(
        sourceCode: String
    ): RedcodeProgram {
        return try {
            RedcodeParser.parse(sourceCode)
        } catch (e: Throwable) {
            RedcodeProgram.empty()
        }
    }

    fun step() {
        isExecuting = false
        program = program.step()
    }

    fun reset() {
        isExecuting = false
//        setSourceCode(sourceCode)
    }
}

@Composable
fun Interpreter(
    sourceCode: String,
    modifier: Modifier = Modifier
) {
    val interpreter = remember { InterpreterState(sourceCode) }
    val scrollState = rememberLazyListState()
    LaunchedEffect(sourceCode) { interpreter.setSourceCode(sourceCode) }
    LaunchedEffect(interpreter.scrollPosition) { scrollState.scrollToItem(interpreter.scrollPosition) }
    LaunchedEffect(interpreter.isExecuting) {
        launch(Dispatchers.Default) {
            while (interpreter.program.isRunning && interpreter.isExecuting) {
                delay(5)
                interpreter.step()
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
                LazyColumn(state = scrollState) {
                    item { Spacer(Modifier.height(16.dp)) }
                    itemsIndexed(interpreter.program.commands) { index, item ->
                        TableCommandRow(index, item)
                    }
                }
            }
            Column(
                modifier = Modifier.weight(0.3f),
                content = {
                    ButtonsColumn(
                        jumpTo = interpreter.scrollPosition,
                        line = interpreter.currentLine,
                        onSingleStepClick = {
                            interpreter.step()
                        },
                        onExecuteClick = {
                            interpreter.isExecuting = true
                        },
                        onStopClick = {
                            interpreter.isExecuting = false
                        },
                        onResetClick = {
                            interpreter.reset()
                        },
                        onJumpToClick = { position ->
                            interpreter.scrollPosition = position
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
fun ActionButton(text: String, onClick: () -> Unit = {}) {
    OutlinedButton(onClick, shape = RectangleShape, modifier = Modifier.fillMaxWidth()) {
        Text(text)
    }
}

@Composable
@Preview
private fun PreviewInterpreter() {
    RedcodeTheme {
        InterpreterState(TestUtils.simpleProgram())
    }
}

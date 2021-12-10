package ui.interpreter

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
import androidx.compose.ui.unit.sp
import model.Address
import model.Command
import theme.LightGrayShade
import theme.RedcodeTheme
import theme.RedcodeTheme.body3
import ui.interpreter.model.ActionsState
import ui.interpreter.model.InterpreterState

@Composable
fun Interpreter(
    sourceCode: String,
    modifier: Modifier = Modifier
) {
    val interpreter = remember { InterpreterState() }
    val scrollState = rememberLazyListState()
    val actions = remember {
        ActionsState(
            onSingleStepClick = { interpreter.step() },
            onExecuteClick = { interpreter.isInExecuteMode = true },
            onStopClick = { interpreter.isInExecuteMode = false },
            onResetClick = { interpreter.reset() },
            onJumpToClick = { position -> interpreter.scrollPosition = position }
        )
    }
    actions.scrollPosition = interpreter.scrollPosition
    actions.currentLine = interpreter.currentLine

    if (interpreter.sourceCode != sourceCode) {
        interpreter.setProgramSourceCode(sourceCode)
    }

    LaunchedEffect(interpreter.scrollPosition) {
        scrollState.scrollToItem(interpreter.scrollPosition)
    }
    LaunchedEffect(interpreter.isInExecuteMode) {
        interpreter.execute()
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = RedcodeTheme.arrangement
    ) {
        Text(
            text = "INTERPRETER",
            style = MaterialTheme.typography.subtitle2
        )
        Row(horizontalArrangement = RedcodeTheme.arrangement) {
            Column(
                modifier = Modifier.weight(0.8f).background(Color.LightGrayShade)
            ) {
                TableHeaderRow()
                LazyColumn(state = scrollState) {
                    item {
                        Spacer(Modifier.height(16.dp))
                    }
                    itemsIndexed(interpreter.program.commands) { index, item ->
                        TableCommandRow(index, item)
                    }
                }
            }
            Column(modifier = Modifier.weight(0.4f).fillMaxHeight()) {
                ButtonsColumn(actions)
            }
        }
    }
}

@Composable
fun TableHeaderRow(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = RedcodeTheme.arrangement
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
        modifier = modifier
            .fillMaxWidth()
            .background(if (command.isSelected) Color.White else Color.LightGrayShade)
    ) {
        TableTextCeil(index.toString())
        TableTextCeil(command.name)
        TableTextCeil(command.operandA.toString())
        TableTextCeil(command.operandB.toString())
    }
}

@Composable
private fun ColumnScope.ButtonsColumn(
    actions: ActionsState,
    modifier: Modifier = Modifier
) {
    var jumpToPosition by remember { mutableStateOf(actions.scrollPosition.toString()) }
    OutlinedActionButton(text = "Run", onClick = actions.onExecuteClick, modifier = modifier)
    OutlinedActionButton(text = "Stop", onClick = actions.onStopClick, modifier = modifier)
    OutlinedActionButton(text = "Next", onClick = actions.onSingleStepClick, modifier = modifier)
    OutlinedActionButton(text = "Reset", onClick = actions.onResetClick, modifier = modifier)
    Spacer(Modifier.height(8.dp))
    TextField(
        value = jumpToPosition,
        label = { Text("Jump to") },
        maxLines = 1,
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.LightGrayShade,
            focusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        onValueChange = { text ->
            jumpToPosition = text
            val value = text.filter(Char::isDigit).toIntOrNull()
            if (value != null) {
                actions.onJumpToClick(value)
            }
        },
        modifier = modifier,
        shape = RectangleShape
    )
    Spacer(Modifier.height(8.dp))
    if (actions.currentLine >= 0) {
        Text(
            text = "LINE: ${actions.currentLine}",
            style = RedcodeTheme.typography.body2,
            fontSize = 13.sp,
            modifier = modifier
        )
    }
    Spacer(Modifier.weight(1f))
    Text(
        text = """
                Автор: Бушуев Никита
                Курс: 4
                Направление: ИСТ
                Дата: 05.12.2021 - 10.12.2021
                Адресация: ${Address.all},
                Команды: ${Command.all}
            """.trimIndent(),
        style = RedcodeTheme.typography.body3,
        color = Color.Gray
    )
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
fun OutlinedActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) = OutlinedButton(
    onClick = onClick,
    shape = RectangleShape,
    modifier = modifier.fillMaxWidth()
) {
    Text(text)
}

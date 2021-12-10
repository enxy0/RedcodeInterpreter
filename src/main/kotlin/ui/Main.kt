// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import common.utils.TestUtils
import theme.RedcodeTheme
import ui.interpreter.Interpreter

@Composable
@Preview
fun App() {
    RedcodeTheme {
        var sourceCode by remember { mutableStateOf(TestUtils.simpleProgram()) }
        Row(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalArrangement = RedcodeTheme.arrangement
        ) {
            Editor(
                text = sourceCode,
                modifier = Modifier.weight(0.4f),
                onTextChange = { text -> sourceCode = text }
            )
            Interpreter(
                sourceCode = sourceCode,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}

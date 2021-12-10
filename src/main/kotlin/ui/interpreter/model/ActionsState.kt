package ui.interpreter.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class ActionsState(
    var onSingleStepClick: () -> Unit = {},
    var onExecuteClick: () -> Unit = {},
    var onStopClick: () -> Unit = {},
    var onResetClick: () -> Unit = {},
    var onJumpToClick: (position: Int) -> Unit = {}
) {
    var scrollPosition: Int by mutableStateOf(0)
    var currentLine: Int by mutableStateOf(0)
}

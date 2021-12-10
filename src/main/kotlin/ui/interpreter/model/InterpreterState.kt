package ui.interpreter.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import common.utils.RedcodeParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import model.RedcodeProgram

class InterpreterState {
    var sourceCode by mutableStateOf("")
        private set
    var program by mutableStateOf(parseSourceCode(sourceCode))
        private set
    var currentLine by mutableStateOf(getCurrentLineInternal(program))
        private set

    var isInExecuteMode by mutableStateOf(false)
    var scrollPosition by mutableStateOf(0)

    fun step() {
        program = program.step()
        currentLine = getCurrentLineInternal(program)
    }

    fun setProgramSourceCode(src: String) {
        sourceCode = src
        program = parseSourceCode(sourceCode)
        currentLine = getCurrentLineInternal(program)
    }

    suspend fun execute(delay: Long = 0L) = withContext(Dispatchers.Default) {
        while (program.isRunning && isInExecuteMode) {
            delay(delay)
            step()
        }
    }

    fun reset() {
        scrollPosition = 0
        isInExecuteMode = false
        program = parseSourceCode(sourceCode)
        currentLine = getCurrentLineInternal(program)
    }

    private fun getCurrentLineInternal(
        program: RedcodeProgram
    ) = program.commands.indexOfFirst { command -> command.isSelected }

    private fun parseSourceCode(
        sourceCode: String
    ): RedcodeProgram = try {
        RedcodeParser.parse(sourceCode)
    } catch (e: Throwable) {
        RedcodeProgram.empty()
    }
}

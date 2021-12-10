import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import model.RedcodeProgram

class ViewModel {
    private val _program = MutableStateFlow(RedcodeProgram.empty())
    val program: StateFlow<RedcodeProgram> = _program.asStateFlow()

    fun parseSourceCode(sourceCode: String) {

    }
}

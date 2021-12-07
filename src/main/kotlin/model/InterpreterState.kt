package model

sealed class InterpreterState(open val program: RedcodeProgram) {
    data class Initial(override val program: RedcodeProgram) : InterpreterState(program)
    data class OneStep(override val program: RedcodeProgram) : InterpreterState(program)
    data class Run(override val program: RedcodeProgram) : InterpreterState(program)
}
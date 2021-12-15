package common.utils

object TestUtils {
    fun simpleProgram() = """
        ORG 3
        DAT 5
        DAT -2
        DAT #1, #3
        ADD ${'$'}-1, ${'$'}-2
        JMP 3
        DAT -5
        MOV 0, 1
        SUB @-2, >-7
        JMP 2
        DAT -2
        JMP <-1
    """.trimIndent()
}

package domain

import model.Address
import model.Command
import model.RedcodeProgram
import model.RedcodeProgram.Companion.COMMANDS_MAX

object RedcodeParser {

    private const val COMMANDS_COMMON_SIZE = 20

    private val emptyCommands: List<Command> by lazy {
        generateSequence { Command.Dat.default() }.take(COMMANDS_MAX).toList()
    }

    fun parse(text: String): RedcodeProgram {
        var org: Command.Org? = null
        val commands = ArrayList<Command>(COMMANDS_COMMON_SIZE)
        val lines = text.splitToSequence('\n').filterNot(String::isEmpty).map(String::trim)
        for (line in lines) {
            commands += when {
                line matches Command.Org.regex -> {
                    val (_, operand) = parseLine(line)
                    org = Command.Org(
                        operandB = parseAddressing(operand) { number ->
                            Address.Immediate(number)
                        } as Address.Immediate
                    )
                    continue
                }
                line matches Command.Dat.regex -> {
                    val (_, operandA, operandB) = parseLine(line)
                    Command.Dat(
                        operandA = parseAddressing(operandA.takeIf { operandB.isNotBlank() }) { number ->
                            Address.Immediate(number)
                        } as? Address.Immediate ?: Address.Immediate(0),
                        operandB = parseAddressing(operandB.ifBlank { operandA }) { number ->
                            Address.Immediate(number)
                        } as Address.Immediate
                    )
                }
                line matches Command.Move.regex -> {
                    val (_, operandA, operandB) = parseLine(line)
                    Command.Move(
                        operandA = parseAddressing(operandA)!!,
                        operandB = parseAddressing(operandB)!!
                    )
                }
                line matches Command.Add.regex -> {
                    val (_, operandA, operandB) = parseLine(line)
                    Command.Add(
                        operandA = parseAddressing(operandA)!!,
                        operandB = parseAddressing(operandB)!!
                    )
                }
                line matches Command.Sub.regex -> {
                    val (_, operandA, operandB) = parseLine(line)
                    Command.Sub(
                        operandA = parseAddressing(operandA)!!,
                        operandB = parseAddressing(operandB)!!
                    )
                }
                line matches Command.Mul.regex -> {
                    val (_, operandA, operandB) = parseLine(line)
                    Command.Mul(
                        operandA = parseAddressing(operandA)!!,
                        operandB = parseAddressing(operandB)!!
                    )
                }
                line matches Command.Div.regex -> {
                    val (_, operandA, operandB) = parseLine(line)
                    Command.Div(
                        operandA = parseAddressing(operandA)!!,
                        operandB = parseAddressing(operandB)!!
                    )
                }
                line matches Command.Mod.regex -> {
                    val (_, operandA, operandB) = parseLine(line)
                    Command.Mod(
                        operandA = parseAddressing(operandA)!!,
                        operandB = parseAddressing(operandB)!!
                    )
                }
                line matches Command.Jmp.regex -> {
                    val (_, operandA, operandB) = parseLine(line)
                    Command.Jmp(
                        operandA = parseAddressing(operandA)!!,
                        operandB = parseAddressing(operandB) ?: Address.Direct(0)
                    )
                }
                line matches Command.Jmz.regex -> {
                    val (_, operandA, operandB) = parseLine(line)
                    Command.Jmz(
                        operandA = parseAddressing(operandA)!!,
                        operandB = parseAddressing(operandB)!!
                    )
                }
                line matches Command.Jmn.regex -> {
                    val (_, operandA, operandB) = parseLine(line)
                    Command.Jmn(
                        operandA = parseAddressing(operandA)!!,
                        operandB = parseAddressing(operandB)!!
                    )
                }
                line matches Command.Djn.regex -> {
                    val (_, operandA, operandB) = parseLine(line)
                    Command.Djn(
                        operandA = parseAddressing(operandA)!!,
                        operandB = parseAddressing(operandB)!!
                    )
                }
                line matches Command.Cmp.regex -> {
                    val (_, operandA, operandB) = parseLine(line)
                    Command.Cmp(
                        operandA = parseAddressing(operandA)!!,
                        operandB = parseAddressing(operandB)!!
                    )
                }
                line matches Command.Slt.regex -> {
                    val (_, operandA, operandB) = parseLine(line)
                    Command.Slt(
                        operandA = parseAddressing(operandA)!!,
                        operandB = parseAddressing(operandB)!!
                    )
                }
                else -> {
                    Command.Dat.default()
                }
            }
        }
        val number = org?.operandB?.number ?: 0
        val command = commands[number]
        command.isSelected = command !is Command.Dat
        return RedcodeProgram(commands + emptyCommands.takeLast(COMMANDS_MAX - commands.size))
    }

    private fun parseLine(text: String): List<String> =
        text.split(", ", " ", ",")
            .filter(String::isNotBlank)
            .let { it + generateSequence { "" }.take((3 - it.size).coerceAtLeast(0)) }

    private fun parseAddressing(
        text: String?,
        default: (Int) -> Address = { Address.Direct(it) }
    ): Address? = text?.let {
        try {
            val (addressing, number) = if (Address.regex in text) {
                text.first() to text.drop(1).toInt()
            } else {
                "" to text.toInt()
            }
            when (addressing) {
                '#' -> Address.Immediate(number)
                '@' -> Address.BFieldIndirect(number)
                '<' -> Address.BFieldIndirect.WithPredecrement(number)
                '>' -> Address.BFieldIndirect.WithPostincrement(number)
                '$' -> Address.Direct(number)
                "" -> default(number)
                else -> error("Unsupported addressing or invalid code")
            }
        } catch (e: Exception) {
            null
        }
    }
}

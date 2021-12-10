package model

data class RedcodeProgram(
    val commands: List<Command>
) {
    companion object {
        const val COMMANDS_MAX = 8000
        fun empty() = RedcodeProgram(mutableListOf())
    }

    val isRunning: Boolean
        get() = commands.any { it.isSelected }

    fun step(): RedcodeProgram {
        val localCommands = commands.toMutableList()
        val currentIndex = localCommands
            .indexOfFirst { command -> command.isSelected }
            .takeIf { it >= 0 }
            ?: return this
        // Execute command
        var nextIndex = getSafeIndex(currentIndex + 1)
        val currentCommand = localCommands[currentIndex]
        val (indexA, commandA) = getCommandByAddr(localCommands, currentIndex, currentCommand.operandA)
        val (indexB, commandB) = getCommandByAddr(localCommands, currentIndex, currentCommand.operandB)
        when (currentCommand) {
            is Command.Add -> {
                if (isValidIndex(indexB)) {
                    val operandB = commandB.operandB.copy(commandB.operandB.number + commandA.operandB.number)
                    localCommands[indexB] = commandB.copy(operandB = operandB)
                } else {
                    println("step: $currentCommand cannot be executed for index: $indexB")
                }
            }
            is Command.Sub -> {
                if (isValidIndex(indexB)) {
                    val operandB = commandB.operandB.copy(commandB.operandB.number - commandA.operandB.number)
                    localCommands[indexB] = commandB.copy(operandB = operandB)
                } else {
                    println("step: $currentCommand cannot be executed for index: $indexB")
                }
            }
            is Command.Mul -> {
                if (isValidIndex(indexB)) {
                    val operandB = commandB.operandB.copy(commandB.operandB.number * commandA.operandB.number)
                    localCommands[indexB] = commandB.copy(operandB = operandB)
                } else {
                    println("step: $currentCommand cannot be executed for index: $indexB")
                }
            }
            is Command.Div -> {
                if (isValidIndex(indexB)) {
                    val operandB = commandB.operandB.copy(commandB.operandB.number / commandA.operandB.number)
                    localCommands[indexB] = commandB.copy(operandB = operandB)
                } else {
                    println("step: $currentCommand cannot be executed for index: $indexB")
                }
            }
            is Command.Jmp -> {
                if (isValidIndex(indexA)) {
                    nextIndex = indexA
                } else {
                    println("step: $currentCommand cannot be executed for index: $indexA")
                }
            }
            is Command.Jmz -> TODO()
            is Command.Mod -> TODO()
            is Command.Move -> {
                if (isValidIndex(indexB)) {
                    localCommands[indexB] = commandA
                } else {
                    println("step: $currentCommand cannot be executed for index: $indexB")
                }
            }
            is Command.Dat -> return this
            is Command.Org -> error("Org should not exist in commands. Something went wrong in parser")
        }
        // Set next selected command
        if (isValidIndex(nextIndex)) {
            for ((index, command) in localCommands.withIndex()) {
                command.isSelected = index == nextIndex && command !is Command.Dat
            }
        }
        return this.copy(commands = localCommands)
    }

    private fun isValidIndex(index: Int): Boolean = index in 0 until COMMANDS_MAX

    private fun getCommandByAddr(
        commands: MutableList<Command>,
        currentIndex: Int,
        addr: Address
    ) = when (addr) {
        is Address.Immediate -> {
            -1 to Command.Dat(addr)
        }
        is Address.Direct -> {
            val index = getSafeIndex(currentIndex + addr.number)
            val command = commands[index]
            index to command.copy()
        }
        is Address.BFieldIndirect -> {
            val tempIndex = getSafeIndex(currentIndex + addr.number)
            val tempCommand = commands[tempIndex]
            when (addr) {
                is Address.BFieldIndirect.WithPredecrement -> {
                    val newCommand = tempCommand.copy(
                        operandB = tempCommand.operandB.copy(tempCommand.operandB.number - 1)
                    )
                    commands[tempIndex] = newCommand
                    val index = getSafeIndex(tempIndex + newCommand.operandB.number)
                    val command = commands[index]
                    index to command.copy()
                }
                is Address.BFieldIndirect.WithPostincrement -> {
                    val newCommand = tempCommand.copy(
                        operandB = tempCommand.operandB.copy(tempCommand.operandB.number + 1)
                    )
                    commands[tempIndex] = newCommand
                    val index = getSafeIndex(tempIndex + tempCommand.operandB.number)
                    val command = commands[index]
                    index to command.copy()
                }
                else -> {
                    val index = getSafeIndex(tempIndex + tempCommand.operandB.number)
                    val command = commands[index]
                    index to command.copy()
                }
            }
        }
    }

    private fun getSafeIndex(index: Int): Int {
        val safeIndex = index % COMMANDS_MAX
        return if (safeIndex < 0) COMMANDS_MAX + index else safeIndex
    }

    override fun toString(): String {
        val commands = commands
            .dropLastWhile { command -> (command as? Command.Dat)?.operandB?.number == 0 }
            .joinToString(separator = "\n\t\t", prefix = "\n\t\t")
        return "commands:$commands"
    }
}

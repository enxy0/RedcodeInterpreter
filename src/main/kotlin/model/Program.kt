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
        var shouldCancelExecution = false
        when (currentCommand) {
            is Command.Add -> {
                val operandB = commandB.operandB.copy(commandB.operandB.number + commandA.operandB.number)
                localCommands[indexB] = commandB.copy(operandB = operandB)
            }
            is Command.Sub -> {
                val operandB = commandB.operandB.copy(commandB.operandB.number - commandA.operandB.number)
                localCommands[indexB] = commandB.copy(operandB = operandB)
            }
            is Command.Mul -> {
                val operandB = commandB.operandB.copy(commandB.operandB.number * commandA.operandB.number)
                localCommands[indexB] = commandB.copy(operandB = operandB)
            }
            is Command.Div -> {
                if (commandA.operandB.number != 0) {
                    val operandB = commandB.operandB.copy(commandB.operandB.number / commandA.operandB.number)
                    localCommands[indexB] = commandB.copy(operandB = operandB)
                } else {
                    shouldCancelExecution = true
                }
            }
            is Command.Mod -> {
                val operandB = commandB.operandB.copy(commandB.operandB.number % commandA.operandB.number)
                localCommands[indexB] = commandB.copy(operandB = operandB)
            }
            is Command.Jmp -> {
                nextIndex = indexA
            }
            is Command.Jmz -> {
                if (commandB.operandB.number == 0) {
                    nextIndex = indexA
                }
            }
            is Command.Jmn -> {
                if (commandB.operandB.number != 0) {
                    nextIndex = indexA
                }
            }
            is Command.Djn -> {
                if (commandB.operandB.number - 1 != 0) {
                    nextIndex = indexA
                }
            }
            is Command.Move -> {
                localCommands[indexB] = commandA
            }
            is Command.Cmp -> {
                val index = currentIndex + 2
                if (commandA.operandB.number == commandB.operandB.number) {
                    nextIndex = index
                }
            }
            is Command.Slt -> {
                val index = currentIndex + 2
                if (commandA.operandB.number < commandB.operandB.number) {
                    nextIndex = index
                }
            }
            is Command.Dat -> return this
            is Command.Org -> error("Org should not exist in commands. Something went wrong in parser")
        }
        // Set next selected command
        if (isValidIndex(nextIndex)) {
            for ((index, command) in localCommands.withIndex()) {
                if (index == nextIndex && command !is Command.Dat && !shouldCancelExecution) {
                    localCommands[index] = command.copy().apply { this.isSelected = true }
                } else {
                    command.isSelected = false
                }
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

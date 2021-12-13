package model

import common.utils.RegexUtils
import common.utils.RegexUtils.doubleOperandRegex
import common.utils.RegexUtils.optionalDoubleOperandRegex

sealed class Command(
    open val name: String,
    open val operandA: Address,
    open val operandB: Address,
    open var isSelected: Boolean = false
) {

    companion object {
        const val all = "ORG, DAT, MOV, ADD, SUB, MUL, DIV, JMZ, JMP, MOD, JMP, JMZ, JMN, DJN, CMP, SLT"
    }

    abstract fun copy(operandA: Address = this.operandA, operandB: Address = this.operandB): Command

    data class Org(
        override val operandB: Address.Immediate,
        override val operandA: Address = Address.Immediate(0),
        override var isSelected: Boolean = false
    ) : Command(name = NAME, operandA = operandA, operandB = operandB) {
        companion object {
            const val NAME = "ORG"
            val regex = "$NAME ${RegexUtils.anyNumberRegex}".toRegex(RegexOption.IGNORE_CASE)
        }

        override fun copy(operandA: Address, operandB: Address): Command = Org(
            operandA = operandA,
            operandB = operandB as Address.Immediate
        )
    }

    data class Dat(
        override val operandB: Address.Immediate,
        override val operandA: Address.Immediate = Address.Immediate(0),
        override var isSelected: Boolean = false
    ) : Command(name = NAME, operandA = operandA, operandB = operandB) {
        companion object {
            const val NAME = "DAT"
            val regex = "$NAME $optionalDoubleOperandRegex".toRegex(RegexOption.IGNORE_CASE)

            fun default() = Dat(Address.Immediate(0), Address.Immediate(0))
        }

        override fun copy(operandA: Address, operandB: Address): Command = Dat(
            operandA = operandA as Address.Immediate,
            operandB = operandB as Address.Immediate
        )
    }

    data class Move(
        override val operandA: Address,
        override val operandB: Address,
        override var isSelected: Boolean = false
    ) : Command(name = NAME, operandA = operandA, operandB = operandB) {
        companion object {
            const val NAME = "MOV"
            val regex = "$NAME $doubleOperandRegex".toRegex(RegexOption.IGNORE_CASE)
        }

        override fun copy(operandA: Address, operandB: Address): Command = Move(operandA, operandB)
    }

    data class Add(
        override val operandA: Address,
        override val operandB: Address,
        override var isSelected: Boolean = false
    ) : Command(name = NAME, operandA = operandA, operandB = operandB) {
        companion object {
            const val NAME = "ADD"
            val regex = "$NAME $doubleOperandRegex".toRegex(RegexOption.IGNORE_CASE)
        }

        override fun copy(operandA: Address, operandB: Address): Command = Add(
            operandA = operandA,
            operandB = operandB
        )
    }

    data class Sub(
        override val operandA: Address,
        override val operandB: Address,
        override var isSelected: Boolean = false
    ) : Command(name = NAME, operandA = operandA, operandB = operandB) {
        companion object {
            const val NAME = "SUB"
            val regex = "$NAME $doubleOperandRegex".toRegex(RegexOption.IGNORE_CASE)
        }

        override fun copy(operandA: Address, operandB: Address): Command = Add(
            operandA = operandA,
            operandB = operandB
        )
    }

    data class Mul(
        override val operandA: Address,
        override val operandB: Address,
        override var isSelected: Boolean = false
    ) : Command(name = NAME, operandA = operandA, operandB = operandB) {
        companion object {
            const val NAME = "MUL"
            val regex = "$NAME $doubleOperandRegex".toRegex(RegexOption.IGNORE_CASE)
        }

        override fun copy(operandA: Address, operandB: Address): Command = Add(
            operandA = operandA,
            operandB = operandB
        )
    }

    data class Div(
        override val operandA: Address,
        override val operandB: Address,
        override var isSelected: Boolean = false
    ) : Command(name = NAME, operandA = operandA, operandB = operandB) {
        companion object {
            const val NAME = "DIV"
            val regex = "$NAME $doubleOperandRegex".toRegex(RegexOption.IGNORE_CASE)
        }

        override fun copy(operandA: Address, operandB: Address): Command = Add(
            operandA = operandA,
            operandB = operandB
        )
    }

    data class Jmz(
        override val operandA: Address,
        override val operandB: Address,
        override var isSelected: Boolean = false
    ) : Command(name = NAME, operandA = operandA, operandB = operandB) {
        companion object {
            const val NAME = "JMZ"
            val regex = "$NAME $doubleOperandRegex".toRegex(RegexOption.IGNORE_CASE)
        }

        override fun copy(operandA: Address, operandB: Address): Command = Jmz(
            operandA = operandA,
            operandB = operandB
        )
    }

    data class Jmn(
        override val operandA: Address,
        override val operandB: Address,
        override var isSelected: Boolean = false
    ) : Command(name = NAME, operandA = operandA, operandB = operandB) {
        companion object {
            const val NAME = "JMN"
            val regex = "$NAME $doubleOperandRegex".toRegex(RegexOption.IGNORE_CASE)
        }

        override fun copy(operandA: Address, operandB: Address): Command = Jmn(
            operandA = operandA,
            operandB = operandB
        )
    }

    data class Djn(
        override val operandA: Address,
        override val operandB: Address,
        override var isSelected: Boolean = false
    ) : Command(name = NAME, operandA = operandA, operandB = operandB) {
        companion object {
            const val NAME = "DJN"
            val regex = "$NAME $doubleOperandRegex".toRegex(RegexOption.IGNORE_CASE)
        }

        override fun copy(operandA: Address, operandB: Address): Command = Djn(
            operandA = operandA,
            operandB = operandB
        )
    }

    data class Jmp(
        override val operandA: Address,
        override val operandB: Address = Address.Direct(0),
        override var isSelected: Boolean = false
    ) : Command(name = NAME, operandA = operandA, operandB = operandB) {
        companion object {
            const val NAME = "JMP"
            val regex = "$NAME $optionalDoubleOperandRegex".toRegex(RegexOption.IGNORE_CASE)
        }

        override fun copy(operandA: Address, operandB: Address): Command = Jmp(
            operandA = operandA,
            operandB = operandB as Address.Direct
        )
    }

    data class Mod(
        override val operandA: Address,
        override val operandB: Address,
        override var isSelected: Boolean = false
    ) : Command(name = NAME, operandA = operandA, operandB = operandB) {
        companion object {
            const val NAME = "MOD"
            val regex = "$NAME $doubleOperandRegex".toRegex(RegexOption.IGNORE_CASE)
        }

        override fun copy(operandA: Address, operandB: Address): Command = Mod(
            operandA = operandA,
            operandB = operandB
        )
    }

    data class Cmp(
        override val operandA: Address,
        override val operandB: Address,
        override var isSelected: Boolean = false
    ) : Command(name = NAME, operandA = operandA, operandB = operandB) {
        companion object {
            const val NAME = "CMP"
            val regex = "$NAME $doubleOperandRegex".toRegex(RegexOption.IGNORE_CASE)
        }

        override fun copy(operandA: Address, operandB: Address): Command = Cmp(
            operandA = operandA,
            operandB = operandB
        )
    }

    data class Slt(
        override val operandA: Address,
        override val operandB: Address,
        override var isSelected: Boolean = false
    ) : Command(name = NAME, operandA = operandA, operandB = operandB) {
        companion object {
            const val NAME = "SLT"
            val regex = "$NAME $doubleOperandRegex".toRegex(RegexOption.IGNORE_CASE)
        }

        override fun copy(operandA: Address, operandB: Address): Command = Slt(
            operandA = operandA,
            operandB = operandB
        )
    }
}

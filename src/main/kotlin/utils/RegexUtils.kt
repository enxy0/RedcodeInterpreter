package utils

import model.Address

object RegexUtils {
    val anyNumberRegex = "[-0-9]+".toRegex()
    val operandRegex = "${Address.regex}?$anyNumberRegex".toRegex()
    val doubleOperandRegex = "$operandRegex(,|, +)$operandRegex".toRegex()
}
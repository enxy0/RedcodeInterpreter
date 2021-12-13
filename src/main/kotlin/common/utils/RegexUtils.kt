package common.utils

import model.Address

object RegexUtils {
    val anyNumberRegex = "[-0-9]+".toRegex()
    val splitRegex = "(,|, +)".toRegex()
    val operandRegex = "${Address.regex}?$anyNumberRegex".toRegex()
    val doubleOperandRegex = "$operandRegex(,|, +)$operandRegex".toRegex()
    val optionalDoubleOperandRegex = "(${Address.regex}?$anyNumberRegex)($splitRegex${Address.regex}?$anyNumberRegex)?".toRegex()
}

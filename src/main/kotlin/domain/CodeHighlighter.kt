package domain

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import model.Command
import theme.PinkCodeKeyword

object CodeHighlighter {

    fun highlight(sourceCode: String): AnnotatedString {
        val keywords = Command.all.split(", ")
        val coloredKeywords = sortedSetOf<Pair<Int, Int>>(compareBy { it.first })
        keywords.forEach { keyword ->
            var lastIndex = 0
            do {
                val startIndex = sourceCode.indexOf(keyword, startIndex = lastIndex, ignoreCase = true)
                val endIndex = startIndex + keyword.length
                if (startIndex in sourceCode.indices && endIndex in sourceCode.indices) {
                    coloredKeywords += startIndex to endIndex
                }
                lastIndex = endIndex
            } while (startIndex in sourceCode.indices)
        }
        val text = buildAnnotatedString {
            var previousPair = 0 to (coloredKeywords.firstOrNull()?.first ?: 0)
            for (coloredKeyword in coloredKeywords) {
                val (startIndex, endIndex) = coloredKeyword
                append(sourceCode.substring(previousPair.second, startIndex))
                withStyle(SpanStyle(color = Color.PinkCodeKeyword, fontWeight = FontWeight.Bold)) {
                    append(sourceCode.substring(startIndex, endIndex))
                }
                previousPair = coloredKeyword
            }
            append(sourceCode.substring(previousPair.second, sourceCode.length))
        }
        return text
    }
}

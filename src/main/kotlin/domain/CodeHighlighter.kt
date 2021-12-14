package domain

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import domain.model.Highlight
import model.Address
import model.Command
import theme.BlueCodeKeyword
import theme.PinkCodeKeyword
import java.util.*

object CodeHighlighter {

    fun highlight(sourceCode: String): AnnotatedString {
        val commands = Command.all.split(", ")
        val addressModes = Address.all.split(", ")
        val highlights = sortedSetOf<Highlight>(compareBy { it.start })
        findHighlightParts(highlights, sourceCode, commands, Color.PinkCodeKeyword)
        findHighlightParts(highlights, sourceCode, addressModes, Color.BlueCodeKeyword)
        val text = buildAnnotatedString {
            var previousPair = Highlight(0, 0, Color.Black, false)
            for (highlight in highlights) {
                val (start, end, color, bold) = highlight
                append(sourceCode.substring(previousPair.end, start))
                withStyle(SpanStyle(color = color, fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal)) {
                    append(sourceCode.substring(start, end))
                }
                previousPair = highlight
            }
            append(sourceCode.substring(previousPair.end, sourceCode.length))
        }
        return text
    }

    private fun findHighlightParts(
        highlights: SortedSet<Highlight>,
        text: String,
        coloredParts: List<String>,
        color: Color
    ) {
        coloredParts.forEach { coloredPart ->
            var last = 0
            do {
                val start = text.indexOf(coloredPart, startIndex = last, ignoreCase = true)
                val end = start + coloredPart.length
                if (start in text.indices && end in text.indices) {
                    highlights += Highlight(start, end, color, false)
                }
                last = end
            } while (start in text.indices)
        }
    }
}

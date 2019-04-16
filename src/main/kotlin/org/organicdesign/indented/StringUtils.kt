package org.organicdesign.indented

import java.io.File

fun <K,V> Pair<K,V>.toEntry() = object: Map.Entry<K,V> {
    override val key: K = first
    override val value: V = second
}

/**
 * The goal of this project is to produce pretty-print indented strings that could compile to Kotlin or Java,
 * or at least look like they could (some abbreviations for brevity).
 */
object StringUtils {
    private val SPACES = arrayOf("",
                                 " ",
                                 "  ",
                                 "   ",
                                 "    ",
                                 "     ",
                                 "      ",
                                 "       ",
                                 "        ",
                                 "         ",
                                 "          ",
                                 "           ",
                                 "            ",
                                 "             ",
                                 "              ",
                                 "               ",
                                 "                ",
                                 "                 ",
                                 "                  ",
                                 "                   ",
                                 "                    ",
                                 "                     ",
                                 "                      ",
                                 "                       ",
                                 "                        ",
                                 "                         ",
                                 "                          ",
                                 "                           ",
                                 "                            ",
                                 "                             ",
                                 "                              ",
                                 "                               ",
                                 "                                ",
                                 "                                 ",
                                 "                                  ",
                                 "                                   ",
                                 "                                    ",
                                 "                                     ",
                                 "                                      ",
                                 "                                       ",
                                 "                                        ",
                                 "                                         ",
                                 "                                          ",
                                 "                                           ",
                                 "                                            ",
                                 "                                             ",
                                 "                                              ",
                                 "                                               ",
                                 "                                                ")

    private val SPACES_LENGTH_MINUS_ONE = SPACES.size - 1

    /**
     * Efficiently returns a String with the given number of spaces.
     * @param len the number of spaces
     * @return a [String] with the specificed number of spaces.
     */
    @JvmStatic
    fun spaces(len: Int): String =
            when {
                len < 0                        -> throw IllegalArgumentException("Can't show negative spaces: $len")
                len <= SPACES_LENGTH_MINUS_ONE -> SPACES[len]
                else                           -> {
                    var remainingLen = len
                    val sB = StringBuilder()
                    while (remainingLen > SPACES_LENGTH_MINUS_ONE) {
                        sB.append(SPACES[SPACES_LENGTH_MINUS_ONE])
                        remainingLen -= SPACES_LENGTH_MINUS_ONE
                    }
                    sB.append(SPACES[remainingLen]).toString()
                }
            }

    @JvmStatic
    fun iterableToStr(indent: Int, collName: String, ls: Iterable<Any?>): String {
        val subIndent: Int = indent + collName.length + 1 // + 1 is for the paren.
        val spaces: String = spaces(subIndent)
        return ls.foldIndexed(StringBuilder(collName)
                                      .append("("),
                              { idx, acc, item ->
                                  if (idx > 0) {
                                      acc.append(",\n")
                                      acc.append(spaces)
                                  }
                                  acc.append(indent(subIndent, item))
                              })
                .append(")")
                .toString()
    }

    @JvmStatic
    fun listToStr(indent: Int, ls: Iterable<Any?>): String {
        val subIndent: Int = indent + 1 // + 1 is for the paren.
        val spaces: String = spaces(subIndent)
        return ls.foldIndexed(StringBuilder("["),
                              { idx, acc, item ->
                                  if (idx > 0) {
                                      acc.append(",\n")
                                      acc.append(spaces)
                                  }
                                  acc.append(indent(subIndent, item))
                              })
                .append("]")
                .toString()
    }

    /**
     * Use this to pretty-print a class with one field per line.
     */
    @JvmStatic
    fun oneFieldPerLine(
            indent: Int,
            collName: String,
            fields: Iterable<Map.Entry<String,Any?>>
    ): String {
        val subIndent: Int = indent + collName.length + 1 // + 1 is for the paren.
        val spaces: String = spaces(subIndent)
        return fields.foldIndexed(StringBuilder(collName)
                                      .append("("),
                              { idx, acc, item ->
                                  if (idx > 0) {
                                      acc.append(",\n")
                                      acc.append(spaces)
                                  }
                                  acc.append(item.key).append("=").append(indent(subIndent, item.value))
                              })
                .append(")")
                .toString()
    }

    /*
     * Kotlin wrapper because Pair does not implement Map.Entry and Pair is not accessible in Java.
     */
    fun oneFieldPerLineK(
            indent: Int,
            collName: String,
            fields: Iterable<Pair<String,Any?>>
    ): String = oneFieldPerLine(indent, collName, fields.map { it.toEntry() })

    @JvmStatic
    fun indent(indent: Int, item: Any?): String =
            when (item) {
                null                  -> "null"
                is IndentedStringable -> item.indentedStr(indent)
                is String             -> stringify(item)
                is Map.Entry<*,*>     -> {
                    val key = indent(indent, item.key)
                    key + "=" + indent(indent + key.length + 1, item.value)
                }
                is Pair<*,*>          -> {
                    val first = indent(indent, item.first)
                    first + " to " + indent(indent + first.length + 4, item.second)
                }
                is Char               -> charToStr(item)
                is Float              -> floatToStr(item)
                is List<*>            -> iterableToStr(indent, "listOf", item)
                is Map<*,*>           -> iterableToStr(indent, "mapOf", item.entries)
                is Set<*>             -> iterableToStr(indent, "setOf", item)
                is Iterable<*>        -> iterableToStr(indent, item::class.java.simpleName, item)
// Interesting, but too much info.
//                is Array<*>           -> iterableToStr(indent, "arrayOf<${item::class.java.componentType.simpleName}>",
//                                                       item.toList())
                is Array<*>           -> iterableToStr(indent, "arrayOf", item.toList())
                is File               -> {
                    val details = StringBuilder()
                    if(item.exists()) {
                        if (item.isHidden) {
                            details.append(" hidden")
                        }
                        if (item.isDirectory) {
                            details.append(" dir")
                        } else if (item.isFile) {
                            details.append(" file")
                        }
                        details.append(" ")
                        details.append(if (item.canRead()) "r" else "_")
                        details.append(if (item.canWrite()) "w" else "_")
                        details.append(if (item.canExecute()) "x" else "_")

                    }
                    val ret = StringBuilder("File(")
                    ret.append(stringify(item.absolutePath))
                    if (details.length > 0) {
                        ret.append(details)
                    }
                    ret.append(")").toString()
                }
                else                  -> item.toString()
            }

    private val codePointStrings: Array<String> = arrayOf(
            "\\u0000",
            "\\u0001",
            "\\u0002",
            "\\u0003",
            "\\u0004",
            "\\u0005",
            "\\u0006",
            "\\u0007", // bell
            "\\b", // backspace
            "\\t", // tab
            "\\n", // newline
            "\\u000b", // vertical tab
            "\\f", // form feed
            "\\r") // carriage return

    /**
     * Used internally to convert chars to strings.  I believe this implements an allow-known-good approach, or at
     * least will render any text safe(ish) for being written to a log file.  Meaning that it will show quotes at
     * beginning and end, backspaces will show as \b, line feeds as \r or \n, and no high or illegal characters
     * will be returned.  It should be a safe Kotlin, and a safe Java string except for the escaped dollar-sign.
     */
    private fun escapeChar(codepoint:Int):String = when {
        Character.isBmpCodePoint(codepoint) -> when {
            (codepoint > 0x19 &&
             Character.isDefined(codepoint))  ->
                when (codepoint) {
                    0x22 -> "\\\""
                    // $ is a special character in Kotlin Strings, but not in Java.
                    // This representation works in both languages.
                    0x24 -> "\\u0024"
//                    0x27 -> "\\\'" // charToStr takes care of this instead.
                    0x5c -> "\\\\"
                    else -> codepoint.toChar().toString()
                }
            codepoint < codePointStrings.size -> codePointStrings[codepoint]
            else -> "\\u${String.format("%04x",codepoint)}" // Pad with leading zeros.
        } else -> { // Above the BMP (> 0xffff)
            // 0x010000 is subtracted from the code point, leaving a 20-bit number in the range 0x000000..0x0FFFFF.
            val cp = codepoint - 0x010000
            // The high ten bits (a number in the range 0x0000..0x03FF) are added to 0xD800 to give the first 16-bit
            // code unit or high surrogate, which will be in the range 0xD800..0xDBFF.
            val highTen = cp shr(10)
            // The low ten bits (also in the range 0x0000..0x03FF) are added to 0xDC00 to give the second 16-bit code
            // unit or low surrogate, which will be in the range 0xDC00..0xDFFF.
            val lowTen = cp and(0x3ff)
            "\\u${String.format("%04x", highTen + 0xD800)}\\u${String.format("%04x", lowTen + 0xdc00)}"
        }
    }

    // Surround strings with double quotes and escape any internal double-quotes
    @JvmStatic
    fun stringify(s: String?): String =
            when (s) {
                null -> "null"
                else -> {
                    val sB = StringBuilder("\"")
                    s.codePoints().forEach {
                        sB.append(escapeChar(it))
                    }
                    sB.append("\"").toString()
                }
            }

    // Surround strings with double quotes and escape any internal double-quotes
    @JvmStatic
    fun charToStr(c: Char?): String =
            when (c) {
                null      -> "null"
                '"' -> "\'\"\'"
                '\'' -> "\'\\'\'"
                else -> "\'" + escapeChar(c.toInt()) + "\'"
            }

    @JvmStatic
    fun floatToStr(f: Float?): String {
        if (f == null) {
            return "null"
        }
        val str = f.toString()
        return if (str.endsWith(".0")) {
            str.substring(0, str.length - 2)
        } else {
            str
        } + "f"
    }

    /**
     * Single-quotes a string for Bash, escaping only single quotes.  Returns '' for both the empty string and null.
     * Will not write out any back-spaces.
     */
    @JvmStatic
    fun bashSingleQuote(s: String?): String {
        if ( (s == null) || s.isEmpty() ) {
            return "''"
        }
        var idx = 0
        val sB = StringBuilder()

        // True if the end of the output up to this point is inside a quote.
        // We need this because single quotes must be escaped *outside* a quoted String.
        // That's Right
        // becomes:
        // 'That'\''s Right'
        // So, in the middle of the String, a single quote is escaped "stuff'\''more"
        // At the end it's:
        // 'boys'\'
        // At the beginning:
        // \''kay'
        // And multiple in the middle:
        // 'abc'\'\'\''def'
        // So we have some state here to tell whether the end of the output so far is inside or outside a quote.
        var outputQuoted = false

        while (idx < s.length) {
            val c = s[idx]
            if (c == '\'') {
                if (outputQuoted) {
                    sB.append("'\\'")
                    outputQuoted = false
                } else {
                    sB.append("\\'")
                }
            } else if (c != '\u0008') { // Don't write out backspace.
                if (outputQuoted) {
                    sB.append(c)
                } else {
                    sB.append("'").append(c)
                    outputQuoted = true
                }
            }
            idx++
        }
        if (outputQuoted) {
            // Close the quote.
            sB.append("'")
        }
        return sB.toString()
    }
}
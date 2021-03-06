package org.organicdesign.indented

import java.io.File

/**
 * Adds a method to Kotlin's Pair to convert it to a Map.Entry.
 */
fun <K,V> Pair<K,V>.toEntry() = object: Map.Entry<K,V> {
    override val key: K = first
    override val value: V = second
}

/**
 * Utility function that returns null when A equals X, otherwise returns A unchanged.
 * This is really just syntactic sugar for brevity.
 */
fun <A> nullWhen(a: A, x: A) =
        when (a) {
            x    -> null
            else -> a
        }

/**
 * Utility function that returns null when f(A) is true, otherwise returns A unchanged.
 * This is really just syntactic sugar for brevity.
 */
fun <A> nullWhen(a: A, f: (A) -> Boolean) =
        when (f.invoke(a)) {
            true -> null
            else -> a
        }

/**
 * Utilities for producing pretty-print indented strings that could nearly compile to Kotlin or Java
 * (some abbreviations for brevity).
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
     * @return a [String] with the specified number of spaces.
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

    /**
     * Pretty-prints any iterable with the given indent and class/field name
     * @param entriesAsSymbols If true, treat Map.Entry keys of type String as symbols (don't quote them).
     *        Also if true and a Map.Entry key is "", print only the value ("" is a sentinel value meaning
     *        to print positional parameters).
     * @param singleLine If true, constrain all sub-collections to a single line.
     */
    @JvmStatic
    @JvmOverloads
    fun iterableToStr(
            indent: Int,
            collName: String,
            ls: Iterable<Any?>,
            entriesAsSymbols: Boolean = false,
            singleLine: Boolean = false
    ): String {
        val subIndent: Int = indent + collName.length + 1 // + 1 is for the paren.
        val spaces: String = spaces(subIndent)
        var needsComma = false
        val sB = StringBuilder(collName).append("(")
        ls.forEach {
            if (needsComma) {
                when {
                    singleLine -> sB.append(", ")
                    else -> sB.append(",\n").append(spaces)
                }
                needsComma = false
            }
            sB.append(indent(subIndent, it,
                             entriesAsSymbols = entriesAsSymbols,
                             singleLine = singleLine))
            needsComma = true
        }
        return sB.append(")").toString()
    }

    /**
     * Pretty-prints any Iterable with the given indent as a list/array.
     * @param singleLine If true, constrain all sub-collections to a single line.
     */
    @JvmStatic
    @JvmOverloads
    @Deprecated("This just seems like a one-off version of iterableToString with dubious merit.",
                replaceWith = ReplaceWith("iterableToStr(indent, \"listOf\", ls, singleLine)",
                "org.organicdesign.indented.StringUtils.iterableToStr"))
    fun listToStr(
            indent: Int,
            ls: Iterable<Any?>,
            singleLine: Boolean = false
    ): String {
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
    ): String = iterableToStr(indent, collName, fields,
                              entriesAsSymbols = true,
                              singleLine = false)

    /**
     * Kotlin wrapper because Pair does not implement Map.Entry and Pair is not accessible in Java.
     */
    fun oneFieldPerLineK(
            indent: Int,
            collName: String,
            fields: Iterable<Pair<String,Any?>>
    ): String = oneFieldPerLine(indent, collName, fields.map { it.toEntry() })

    /**
     * Use this to pretty-print a class with all fields on one line.
     */
    @JvmStatic
    fun fieldsOnOneLine(
            indent: Int,
            collName: String,
            fields: Iterable<Map.Entry<String,Any?>>
    ): String {
        var needsComma = false
        val sB = StringBuilder(collName).append("(")
        fields.forEach{
            if (needsComma) {
                sB.append(", ")
                needsComma = false
            }

            sB.append(when (val itemKey = it.key) {
                          // If we want fields on one line, should sub-fields go on one line too?
                          "" -> indent(indent + sB.length, it.value, singleLine = true)
                          else -> "$itemKey=" + indent(indent + sB.length + itemKey.length + 1, it.value,
                                                       singleLine = true)
                      })
            needsComma = true
        }
        return sB.append(")").toString()
    }

    /**
     * Kotlin wrapper because Pair does not implement Map.Entry and Pair is not accessible in Java.
     */
    fun fieldsOnOneLineK(
            indent: Int,
            collName: String,
            fields: Iterable<Pair<String,Any?>>
    ): String = fieldsOnOneLine(indent, collName, fields.map { it.toEntry() })

    /**
     * Takes a shot at pretty-printing anything you throw at it.
     * If it's already an [IndentedStringable], it calls [IndentedStringable.indentedStr].
     * Otherwise takes its best shot at indenting whatever it finds.
     * @param entriesAsSymbols If true, treat Map.Entry keys of type String as symbols (don't quote them).
     *        Also if true and a Map.Entry key is "", print only the value ("" is a sentinel value meaning
     *        to print positional parameters).
     * @param singleLine If true, constrain all sub-collections to a single line.
     */
    @JvmStatic
    @JvmOverloads
    fun indent(
            indent: Int,
            item: Any?,
            entriesAsSymbols: Boolean = false,
            singleLine: Boolean = false
    ): String =
            when (item) {
                null                  -> "null"
                is IndentedStringable -> item.indentedStr(indent)
                is String             -> stringify(item)
                is Map.Entry<*,*>     -> {
                    val itemKey = item.key
                    when {
                        entriesAsSymbols -> {
                            when (itemKey) {
                                // Blank string suppresses key from printing at all in entriesAsSymbols mode
                                // for showing unnamed parameters (in order)
                                "" -> {
                                    indent(indent, item.value)
                                }
                                is String -> {
                                    itemKey + "=" + indent(indent + itemKey.length + 1, item.value)
                                }
                                else -> {
                                    throw IllegalStateException("When entriesAsSymbols is set, the entries must be Strings!")
                                }
                            }
                        }
                        else -> {
                            val key: String = indent(indent, item.key)
                            key + "=" + indent(indent + key.length + 1, item.value)
                        }
                    }
                }
                is Pair<*,*>          -> {
                    val first = indent(indent, item.first)
                    first + " to " + indent(indent + first.length + 4, item.second)
                }
                is Char               -> charToStr(item)
                is Float              -> floatToStr(item)
                is List<*>            -> iterableToStr(indent, "listOf", item, singleLine = singleLine)
                is Map<*,*>           -> iterableToStr(indent, "mapOf", item.entries, singleLine = singleLine)
                is Set<*>             -> iterableToStr(indent, "setOf", item, singleLine = singleLine)
                is Iterable<*>        -> iterableToStr(indent, item::class.java.simpleName, item, singleLine = singleLine)
// Interesting, but too much info.
//                is Array<*>           -> iterableToStr(indent, "arrayOf<${item::class.java.componentType.simpleName}>",
//                                                       item.toList())
                is Array<*>           -> iterableToStr(indent, "arrayOf", item.toList(), singleLine = singleLine)
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
                    ret.append(stringify(item.canonicalPath))
                    if (details.isNotEmpty()) {
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

    /** Surrounds strings with double quotes and escapes any internal double-quotes */
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

    /** Surround chars with single quotes and escape any internal single- or double-quotes */
    @JvmStatic
    fun charToStr(c: Char?): String =
            when (c) {
                null      -> "null"
                '"' -> "\'\"\'"
                '\'' -> "\'\\'\'"
                else -> "\'" + escapeChar(c.toInt()) + "\'"
            }

    /** Prints float so it looks like a Float and not a Double. */
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
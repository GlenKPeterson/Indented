package org.organicdesign.indented

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
     * Creates a new StringBuilder with the given number of spaces and returns it.
     * @param len the number of spaces
     * @return a [StringBuilder] with the specificed number of initial spaces.
     */
    @JvmStatic
    fun spaces(len: Int): StringBuilder {
        val sB = StringBuilder()
        if (len > SPACES_LENGTH_MINUS_ONE) {
            var remainingLen = len
            while (remainingLen > SPACES_LENGTH_MINUS_ONE) {
                sB.append(SPACES[SPACES_LENGTH_MINUS_ONE])
                remainingLen -= SPACES_LENGTH_MINUS_ONE
            }
            return sB.append(SPACES[remainingLen])
        } else if (len < 1) {
            return sB
        }

        return sB.append(SPACES[len])
    }

    @JvmStatic
    fun iterableToStr(indent: Int, collName: String, ls: Iterable<Any>): String {
        val subIndent: Int = indent + collName.length + 1 // + 1 is for the paren.
        val spaces: String = spaces(subIndent).toString()
        return ls.foldIndexed(StringBuilder(spaces(indent))
                                      .append(collName)
                                      .append("("),
                              { idx, acc, item ->
                                  if (idx > 0) {
                                      acc.append(",\n")
                                      acc.append(spaces)
                                  }
                                  acc.append(objToStr(subIndent, item))
                              })
                .append(")")
                .toString()
    }

    @JvmStatic
    fun listToStr(indent: Int, ls: Iterable<Any>): String {
        val subIndent: Int = indent + 1 // + 1 is for the paren.
        val spaces: String = spaces(subIndent).toString()
        return ls.foldIndexed(StringBuilder(spaces(indent))
                                      .append("["),
                              { idx, acc, item ->
                                  if (idx > 0) {
                                      acc.append(",\n")
                                      acc.append(spaces)
                                  }
                                  acc.append(objToStr(subIndent, item))
                              })
                .append("]")
                .toString()
    }

    @JvmStatic
    fun objToStr(indent: Int, item: Any?): String =
            when (item) {
                null                  -> "null"
                is IndentedStringable -> item.indentedStr(indent)
                is String             -> stringify(item)
                is Char               -> charToStr(item)
                is Float              -> floatToStr(item)
                else                  -> item.toString()
            }

//    @JvmStatic
//    fun objToStr(item: Any?): String = when (item) {
//        null      -> "null"
//        is String -> stringify(item)
//        is Char   -> charToStr(item)
//        is Float  -> floatToStr(item)
//        else      -> item.toString()
//    }

    // Surround strings with double quotes and escape any internal double-quotes
    @JvmStatic
    fun stringify(o: String?): String =
            when (o) {
                null      -> "null"
                else -> "\"" + o.replace("\"", "\\\"").replace("\n", "\\n") + "\""
            }

    // Surround strings with double quotes and escape any internal double-quotes
    @JvmStatic
    fun charToStr(c: Char?): String =
            when (c) {
                null      -> "null"
                '\''      -> "'\''"
                else -> "\'" + c + "\'"
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
}
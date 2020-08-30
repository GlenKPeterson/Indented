package org.organicdesign.indented

/**
 * Adds a method like toString, but indented for pretty-printing.  Use StringUtils to implement this method.
 */
interface IndentedStringable {

    /**
     * Implement this using the implementations in [StringUtils] as a guide to pretty-print
     * Indented data structures for joyful debugging.
     * If you aren't then using `.toString()` for anything else, implement by calling `indentedStr(0)`.
     */
    fun indentedStr(indent:Int):String
}
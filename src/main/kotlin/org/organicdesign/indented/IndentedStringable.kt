package org.organicdesign.indented

/**
 * Adds a method like toString, but indented for pretty-printing.  Use StringUtils to implement this method.
 */
interface IndentedStringable {
    fun indentedStr(indent:Int):String
}
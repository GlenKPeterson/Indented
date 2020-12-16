package org.organicdesign.indented

/**
 * If your class makes a data structure, then indented output
 * showing any tree structures will greatly ease debugging.
 * Be careful about circular references (A writes out B which writes out A).
 * In a tree, a child node should print out the parentId instead of the full dump of the parent
 * (which would include the child and loop infinitely).
 *
 * Use the the methods in [StringUtils] to implement this and pretty-print
 * Indented data structures for joyful debugging.
 *
 * You'll typically then want to implement `.toString()` by calling `indentedStr(0)`.
 * If your class's reason for existence is to generate a string, you should probably use that
 * string for debugging *instead* of implementing this interface.
 */
interface IndentedStringable {

    /**
     * Like toString() but indented to show the structure of your data.
     */
    fun indentedStr(indent:Int):String
}
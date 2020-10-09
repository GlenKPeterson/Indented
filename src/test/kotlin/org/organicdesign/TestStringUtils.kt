package org.organicdesign

import org.junit.Assert
import org.organicdesign.indented.IndentedStringable
import org.organicdesign.indented.StringUtils.bashSingleQuote
import org.organicdesign.indented.StringUtils.charToStr
import org.organicdesign.indented.StringUtils.fieldsOnOneLineK
import org.organicdesign.indented.StringUtils.floatToStr
import org.organicdesign.indented.StringUtils.indent
import org.organicdesign.indented.StringUtils.iterableToStr
import org.organicdesign.indented.StringUtils.oneFieldPerLineK
import org.organicdesign.indented.StringUtils.spaces
import org.organicdesign.indented.StringUtils.stringify
import org.organicdesign.indented.nullWhen
import org.organicdesign.indented.toEntry
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class TestStringUtils {
    @Suppress("MemberVisibilityCanBePrivate")
    class Node(val left: Node?,
               val i: Int,
               val right: Node?): IndentedStringable {

        constructor(i: Int) : this(null, i, null)

        override fun indentedStr(indent: Int): String =
                if (left == null && right == null) {
                    "Node($i)"
                } else {
                    "Node(${left?.indentedStr(indent + 5) ?: "null"},\n" +
                    "${spaces(indent + 5)}$i,\n" +
                    "${spaces(indent + 5)}${right?.indentedStr(indent + 5) ?: "null"})"
                }
    }

    @Test
    fun testNullWhen() {
        assertNotNull(nullWhen(2, 3))
        assertNull(nullWhen(2, 2))

        assertNotNull(nullWhen("a", "b"))
        assertNull(nullWhen("hi", "hi"))

        assertNotNull(nullWhen("x", { it == "y" }))
        assertNull(nullWhen("x", { it == "x" }))

        assertNotNull(nullWhen(2, { it > 2 }))
        assertNull(nullWhen(2, { it > 1 }))
    }

    // Here's an example of how to make a data structure visible using a very simple binary tree.
    // The "Node" class is defined above.  Now the data structure looks like itself - and compiles to itself!
    // It's homoiconic!
    @Test fun testNode() {
        assertEquals("Node(Node(Node(1),\n" +
                     "          2,\n" +
                     "          Node(3)),\n" +
                     "     4,\n" +
                     "     Node(Node(5),\n" +
                     "          6,\n" +
                     "          Node(7)))",
                     Node(Node(Node(1),
                               2,
                               Node(3)),
                          4,
                          Node(Node(5),
                               6,
                               Node(7))).indentedStr(0))

        assertEquals("Node(Node(null,\n" +
                     "          1,\n" +
                     "          Node(2)),\n" +
                     "     3,\n" +
                     "     Node(Node(4),\n" +
                     "          5,\n" +
                     "          null))",
                     Node(Node(null,
                               1,
                               Node(2)),
                          3,
                          Node(Node(4),
                               5,
                               null)).indentedStr(0))
    }

    @Test fun testSpaces() {
        assertEquals("", spaces(0))
        assertEquals(" ", spaces(1))
        assertEquals("  ", spaces(2))
        assertEquals("         ", spaces(9))
        assertEquals(99, spaces(99).length)
        assertEquals(99999, spaces(99999).length)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testSpacesEx1() { spaces(-1) }

    @Test(expected = IllegalArgumentException::class)
    fun testSpacesEx2() { spaces(-999) }

    @Test
    fun testSimpleTypes() {
        assertEquals("null", stringify(null))

        assertEquals("\"Hello World\"", stringify("Hello World"))

        assertEquals("\"The quick brown fox jumps over the lazy dog.\"",
                     stringify("The quick brown fox jumps over the lazy dog."))

        assertEquals("\"A: Hi, \\\"there's\\\"\\nB: \\\"Howdy!\\\"\"",
                     stringify("A: Hi, \"there's\"\nB: \"Howdy!\""))

        assertEquals("\"Microsoft uses \\\\ for paths\"",
                     stringify("Microsoft uses \\ for paths"))

        assertEquals("\"We handle \\n and \\r just fine.\"",
                     stringify("We handle \n and \r just fine."))

        assertEquals("\"ᚠᛇᚻ᛫ᛒᛦᚦ᛫ᚠᚱᚩᚠᚢᚱ᛫ᚠᛁᚱᚪ᛫ᚷᛖᚻᚹᛦᛚᚳᚢᛗ\"",
                     stringify("ᚠᛇᚻ᛫ᛒᛦᚦ᛫ᚠᚱᚩᚠᚢᚱ᛫ᚠᛁᚱᚪ᛫ᚷᛖᚻᚹᛦᛚᚳᚢᛗ"))

        assertEquals("\"На берегу пустынных волн\"",
                     stringify("На берегу пустынных волн"))

        assertEquals("\"ಬಾ ಇಲ್ಲಿ ಸಂಭವಿಸು ಇಂದೆನ್ನ ಹೃದಯದಲಿ\"",
                     stringify("ಬಾ ಇಲ್ಲಿ ಸಂಭವಿಸು ಇಂದೆನ್ನ ಹೃದಯದಲಿ"))

        assertEquals("\"いろはにほへど　ちりぬるを\"",
                     stringify("いろはにほへど　ちりぬるを"))

        assertEquals("\"いろはにほへど　ちりぬるを\"",
                     stringify("いろはにほへど　ちりぬるを"))

        // $ is a special character in Kotlin Strings, but not in Java.
        // This representation works in both languages.
        assertEquals("\"\\u0024hello\"",
                     stringify("\$hello"))
        assertEquals("'\\u0024'", charToStr('$'))

        // These are (mostly undefined) low unicode characters.
        assertEquals("\"\\u0000,\\u0001,\\u0002,\\u0003,\\u0004,\\u0005,\\u0006\\u0007\\b\"",
                     stringify("\u0000,\u0001,\u0002,\u0003,\u0004,\u0005,\u0006\u0007\u0008"))
        assertEquals("\"\\t,\\n,\\u000b,\\f,\\r,\\u000e,\\u000f\"",
                     stringify("\u0009,\u000a,\u000b,\u000c,\u000d,\u000e,\u000f"))

        // These are (mostly undefined) high unicode characters.
        assertEquals("\"\\uffff,\\ufff1,\\ufff2,\\ufff3,\\ufff4,\\ufff5,\\ufff6\\ufff7\\ufff8\"",
                     stringify("\uffff,\ufff1,\ufff2,\ufff3,\ufff4,\ufff5,\ufff6\ufff7\ufff8"))
        // The unicode replacement character, followed by two not-a-characters.
        assertEquals("\"�,\\ufffe,\\uffff\"",
                     stringify("\ufffd,\ufffe,\uffff"))

        // 🙂 U+1F642 in utf-16
        assertEquals("\"\\ud83d\\ude42\"",
                     stringify("\ud83d\ude42"))

        // Wikipedia utf-16 example
        assertEquals("\"\\ud801\\udc37\"",
                     stringify("\ud801\udc37"))

        assertEquals("null", floatToStr(null))
        assertEquals("17f", floatToStr(17f))
        assertEquals("1f", floatToStr(1.0f))
        assertEquals("3.14f", floatToStr(3.14f))
        assertEquals("3.14f", floatToStr(3.1400000f))
        assertEquals("100f", floatToStr(100.0f))

        assertEquals("null", charToStr(null))
        assertEquals("'\\''", charToStr('\''))
        assertEquals("'c'", charToStr('c'))

        assertEquals("null", indent(0, null))
        assertEquals("'\\\''", indent(0, '\''))
        assertEquals("\"hello\"=\"hi\"", indent(0, ("hello" to "hi").toEntry()))
        assertEquals("hello=\"hi\"", indent(0, ("hello" to "hi").toEntry(), entriesAsSymbols = true))
        assertEquals("\"hi\"", indent(0, ("" to "hi").toEntry(), entriesAsSymbols = true))
    }

    @Test(expected = IllegalStateException::class)
    fun testNonStringEntriesAsSymbols() {
        indent(0, (7 to "hi").toEntry(), entriesAsSymbols = true)
    }

    @Test(expected = IllegalStateException::class)
    fun testNullEntriesAsSymbols() {
        indent(0, (null to "hi").toEntry(), entriesAsSymbols = true)
    }

    @Test
    fun testIterableToString() {
        assertEquals("Foo(1,\n" +
                     "    2,\n" +
                     "    3)",
                     iterableToStr(0, "Foo", listOf(1, 2, 3)))

        assertEquals("Foo(1, 2, 3)",
                     iterableToStr(0, "Foo", listOf(1, 2, 3), singleLine = true))

        assertEquals("Foo(listOf(\"a\",\n" +
                     "           \"b\",\n" +
                     "           \"c\"),\n" +
                     "    listOf(1,\n" +
                     "           2,\n" +
                     "           3))",
                     iterableToStr(0, "Foo", listOf(
                             listOf("a", "b", "c"),
                             listOf(1, 2, 3))))

        assertEquals("Foo(listOf(\"a\", \"b\", \"c\"), listOf(1, 2, 3))",
                     iterableToStr(0, "Foo", listOf(
                             listOf("a", "b", "c"),
                             listOf(1, 2, 3)), singleLine = true))
    }

    @Test
    fun testCompoundTypes() {
        assertEquals("List(1,\n" +
                     "     2,\n" +
                     "     3)",
                     iterableToStr(0, "List", listOf(1, 2, 3)))

        assertEquals("arrayOf(1,\n" +
                     "        2,\n" +
                     "        3)",
                     indent(0, arrayOf(1, 2, 3)))

//        assertEquals("[1,\n" +
//                     " 2,\n" +
//                     " 3]",
//                     listToStr(0, listOf(1, 2, 3)))
//
//        assertEquals("hey[1,\n" +
//                     "    2,\n" +
//                     "    3]",
//                     "hey" + listToStr(3, listOf(1, 2, 3)))

        assertEquals("helloList(1,\n" +
                     "          2,\n" +
                     "          3)",
                     "hello" + iterableToStr(5, "List", listOf(1, 2, 3)))


        assertEquals("mapOf(\"hello\"=Node(Node(null,\n" +
                     "                        1,\n" +
                     "                        Node(2)),\n" +
                     "                   3,\n" +
                     "                   Node(Node(4),\n" +
                     "                        5,\n" +
                     "                        null)),\n" +
                     "      \"goodbye\"=mapOf(\"world\"=Node(Node(7),\n" +
                     "                                   8,\n" +
                     "                                   Node(9)),\n" +
                     "                      \"hello again\"=listOf(\"the\",\n" +
                     "                                           \"quick\",\n" +
                     "                                           \"brown\",\n" +
                     "                                           \"fox\"),\n" +
                     "                      \"another\"=listOf(1 to 'a',\n" +
                     "                                       2 to 'b',\n" +
                     "                                       3 to 'c')))",
                     iterableToStr(0, "mapOf",
                                   mapOf<Any?,Any?>("hello" to Node(Node(null,
                                                                         1,
                                                                         Node(2)),
                                                                    3,
                                                                    Node(Node(4),
                                                                         5,
                                                                         null)),
                                                    "goodbye" to mapOf<Any?,Any?>("world" to Node(Node(7),
                                                                                                  8,
                                                                                                  Node(9)),
                                                                                  "hello again" to listOf("the",
                                                                                                          "quick",
                                                                                                          "brown",
                                                                                                          "fox"),
                                                                                  "another" to listOf(1 to 'a',
                                                                                                      2 to 'b',
                                                                                                      3 to 'c'))).entries))

    }

    @Test fun testBashStrongQuote() {
        assertEquals("'boys'", bashSingleQuote("boys"))
        assertEquals("'boy'\\''s'", bashSingleQuote("boy's"))
        assertEquals("'boys'\\'", bashSingleQuote("boys'"))
        assertEquals("\\''boys'", bashSingleQuote("'boys"))

        assertEquals("''", bashSingleQuote(null))
        assertEquals("''", bashSingleQuote(""))
        assertEquals("' '", bashSingleQuote(" "))
        assertEquals("\\'", bashSingleQuote("'"))
        assertEquals("\\'\\'", bashSingleQuote("''"))
        assertEquals("\\'\\'\\'", bashSingleQuote("'''"))
        assertEquals("\\'\\'\\''a'", bashSingleQuote("'''a"))
        assertEquals("\\'\\'\\''ab'", bashSingleQuote("'''ab"))
        assertEquals("\\'\\'\\''abc'", bashSingleQuote("'''abc"))
        assertEquals("\\'\\'\\''abc'\\'", bashSingleQuote("'''abc'"))
        assertEquals("\\'\\'\\''abc'\\'\\'", bashSingleQuote("'''abc''"))
        assertEquals("\\'\\'\\''abc'\\'\\'\\'", bashSingleQuote("'''abc'''"))
        assertEquals("\\'\\'\\''abc'\\'\\'\\''d'", bashSingleQuote("'''abc'''d"))
        assertEquals("\\'\\'\\''abc'\\'\\'\\''de'", bashSingleQuote("'''abc'''de"))
        assertEquals("\\'\\'\\''abc'\\'\\'\\''def'", bashSingleQuote("'''abc'''def"))
        assertEquals("\\'\\'\\''abc'\\'\\'\\''def'\\'", bashSingleQuote("'''abc'''def'"))
        assertEquals("\\'\\'\\''abc'\\'\\'\\''def'\\'\\'", bashSingleQuote("'''abc'''def''"))
        assertEquals("\\'\\'\\''abc'\\'\\'\\''def'\\'\\'\\'", bashSingleQuote("'''abc'''def'''"))
        assertEquals("\\'\\'\\''abc'\\'\\'\\''def'\\'\\'\\''g'", bashSingleQuote("'''abc'''def'''g"))
        assertEquals("\\'\\'\\''abc'\\'\\'\\''def'\\'\\'\\''gh'", bashSingleQuote("'''abc'''def'''gh"))
        assertEquals("\\'\\'\\''abc'\\'\\'\\''def'\\'\\'\\''ghi'", bashSingleQuote("'''abc'''def'''ghi"))
        assertEquals("\\'\\'\\''abc'\\'\\'\\''def'\\'\\'\\''ghi'\\'", bashSingleQuote("'''abc'''def'''ghi'"))

        assertEquals("'a'", bashSingleQuote("a"))
        assertEquals("'a'\\'", bashSingleQuote("a'"))
        assertEquals("'a'\\''b'", bashSingleQuote("a'b"))
        assertEquals("'a'\\''b'\\'", bashSingleQuote("a'b'"))
        assertEquals("'a'\\''b'\\''c'", bashSingleQuote("a'b'c"))

        assertEquals("", bashSingleQuote("\u0008"))
        assertEquals("'hi'", bashSingleQuote("\bhi"))
        assertEquals("'hi'", bashSingleQuote("h\u0008i"))
        assertEquals("'hi'", bashSingleQuote("hi\b"))
    }

    @Test fun testFile() {
        val f = File("./src/main/kotlin/org/organicdesign/indented/")
        assertEquals("File(\"${f.canonicalPath}\" dir rwx)",
                     indent(0, f))
    }

    @Test fun testOneFieldPerLine() {
        var f = File("yes")
        assertEquals("  SomeClassName(str=\"hi\",\n" +
                     "                file=File(\"${f.canonicalPath}\"),\n" +
                     "                t=true,\n" +
                     "                f2=false,\n" +
                     "                i=3)",
                     "  " + oneFieldPerLineK(2, "SomeClassName",
                                            listOf("str" to "hi",
                                                   "file" to f,
                                                   "t" to true,
                                                   "f2" to false,
                                                   "i" to 3)))

        // Example that filters out false booleans
        assertEquals("  SomeClassName(str=\"hi\",\n" +
                     "                file=File(\"${f.canonicalPath}\"),\n" +
                     "                t=true,\n" +
                     "                i=3)",
                     "  " + oneFieldPerLineK(2, "SomeClassName",
                                             listOf("str" to "hi",
                                                    "file" to f,
                                                    "t" to true,
                                                    "f2" to false,
                                                    "i" to 3).filter { it.second != false }))

        // Example filters out nulls
        f = File("./src/main/kotlin/org/organicdesign/indented/StringUtils.kt")
        assertEquals("  AnotherClass(str=\"hi\",\n" +
                     "               f=File(\"${f.canonicalPath}\" file rw_),\n" +
                     "               i=3)",
                     "  " + oneFieldPerLineK(2, "AnotherClass",
                                             listOf("str" to "hi",
                                                    "f" to f,
                                                    "g" to null,
                                                    "i" to 3).filter { it.second != null }))

        assertEquals("  MyClass()",
                     "  " + oneFieldPerLineK(2, "MyClass", listOf()))

    }

    @Test
    fun testOneFieldPerLineExample() {
        class CookiePrinter(val a: String, val b: String) {
            override fun toString(): String =
                    "Cookie(${stringify(a)}, ${stringify(b)})"
        }

        Assert.assertEquals("FakeHttpServletResponse(status=404,\n" +
                            "                        committed=true,\n" +
                            "                        redirect=\"somewhere\",\n" +
                            "                        contentType=\"text/html;charset=UTF-8\",\n" +
                            "                        encoding=\"UTF-8\",\n" +
                            "                        locale=zh_TW,\n" +
                            "                        cookies=listOf(Cookie(\"cName\", \"cValue\")),\n" +
                            "                        headers=listOf(\"Hello\"=\"Cupcake\",\n" +
                            "                                       \"Hello\"=\"Pumpkin\",\n" +
                            "                                       \"Buddy\"=\"Rich\",\n" +
                            "                                       \"Two\"=\"2\",\n" +
                            "                                       \"Two\"=\"3\"),\n" +
                            "                        outputStream=FakeServletOutputStream(\"Hello World\"))",
                            oneFieldPerLineK(0, "FakeHttpServletResponse",
                                             listOf("status" to 404,
                                                    "committed" to true,
                                                    "bogus" to null,
                                                    "redirect" to "somewhere",
                                                    "contentType" to "text/html;charset=UTF-8",
                                                    "encoding" to "UTF-8",
                                                    "locale" to object {
                                                        override fun toString(): String = "zh_TW"
                                                    },
                                                    "cookies" to listOf(CookiePrinter("cName", "cValue")),
                                                    "headers" to listOf("Hello" to "Cupcake",
                                                                        "Hello" to "Pumpkin",
                                                                        "Buddy" to "Rich",
                                                                        "Two" to "2",
                                                                        "Two" to "3").map {
                                                        it.toEntry()
                                                    },
                                                    "outputStream" to object {
                                                        override fun toString(): String =
                                                                iterableToStr(0, "FakeServletOutputStream", listOf("Hello World"))
                                                    }
                                             ).filter { it.second != null }))

    }

    @Test
    fun testFieldsOnOneLine() {
        assertEquals("  MyClass(str=\"hi\", t=true, i=3)",
                     "  " + fieldsOnOneLineK(2, "MyClass",
                                             listOf("str" to "hi",
                                                    "t" to true,
                                                    "f" to false,
                                                    "i" to 3)
                                                     .filter{ it.second != false }
                     ))
        assertEquals("  MyClass(str=\"hi\", t=true, z=listOf(1 to \"a\", 2 to \"b\", 3 to \"c\"), i=3)",
                     "  " + fieldsOnOneLineK(2, "MyClass",
                                             listOf("str" to "hi",
                                                    "t" to true,
                                                    "z" to listOf(1 to "a", 2 to "b", 3 to "c"),
                                                    "i" to 3)))

        // "" is a sentinel value to mean an unnamed (positional) parameter.
        // If we want fields on one line, should sub-fields go on one line too?
        assertEquals("  MyClass(\"hi\", \"there\", listOf(1, 2, listOf(\"a\", \"b\")))",
                     "  " + fieldsOnOneLineK(2, "MyClass", listOf("" to "hi",
                                                                  "" to "there",
                                                                  "" to listOf(1, 2, listOf("a", "b")))))
    }

}

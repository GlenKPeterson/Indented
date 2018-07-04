package org.organicdesign

import org.junit.Test
import org.organicdesign.indented.StringUtils.charToStr
import org.organicdesign.indented.StringUtils.floatToStr
import org.organicdesign.indented.StringUtils.iterableToStr
import org.organicdesign.indented.StringUtils.listToStr
import org.organicdesign.indented.StringUtils.objToStr
import org.organicdesign.indented.StringUtils.spaces
import org.organicdesign.indented.StringUtils.stringify
import kotlin.test.assertEquals

class TestStringUtils {
    @Test fun testBasics() {
        assertEquals("[1,\n" +
                     " 2,\n" +
                     " 3]",
                     listToStr(0, listOf(1, 2, 3)))

        assertEquals("List(1,\n" +
                     "     2,\n" +
                     "     3)",
                     iterableToStr(0, "List", listOf(1, 2, 3)))

        assertEquals("   [1,\n" +
                     "    2,\n" +
                     "    3]",
                     listToStr(3, listOf(1, 2, 3)))

        assertEquals("     List(1,\n" +
                     "          2,\n" +
                     "          3)",
                     iterableToStr(5, "List", listOf(1, 2, 3)))

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

        // TODO: Is this a problem?  Will be in Kotlin.
        assertEquals("\"\\\$hello\"",
                     stringify("\$hello"))

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

        assertEquals("null", objToStr(0, null))
        assertEquals("'\\\''", objToStr(0, '\''))

        assertEquals(99, spaces(99).length)
    }

}

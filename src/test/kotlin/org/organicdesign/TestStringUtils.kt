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
        assertEquals("\"A: Hi, \\\"there\\\"\\nB: \\\"Howdy!\\\"\"",
                     stringify("A: Hi, \"there\"\nB: \"Howdy!\""))

        assertEquals("null", floatToStr(null))
        assertEquals("17f", floatToStr(17f))
        assertEquals("1f", floatToStr(1.0f))
        assertEquals("3.14f", floatToStr(3.14f))
        assertEquals("3.14f", floatToStr(3.1400000f))
        assertEquals("100f", floatToStr(100.0f))

        assertEquals("null", charToStr(null))
        assertEquals("'\''", charToStr('\''))
        assertEquals("'c'", charToStr('c'))

        assertEquals("null", objToStr(0, null))
        assertEquals("'\''", objToStr(0, '\''))

        assertEquals(99, spaces(99).length)
    }

}

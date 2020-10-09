package org.organicdesign;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.organicdesign.indented.IndentedStringable;

import static org.junit.Assert.*;
import static org.organicdesign.indented.StringUtils.charToStr;
import static org.organicdesign.indented.StringUtils.spaces;
import static org.organicdesign.indented.StringUtils.stringify;
import static org.organicdesign.indented.StringUtilsKt.nullWhen;

public class TestStringUtilsJava {
    static class Node implements IndentedStringable {
        final Node left;
        final int i;
        final Node right;

        Node(Node l, int value, Node r) { left = l; i = value; right = r; }

        Node(int value) { left = null; i = value; right = null; }


        @NotNull
        public String indentedStr(int indent) {
            if ( left == null &&
                 right == null ) {
                return "Node(" + i + ")";
            }

            return "Node(" +
                   (left == null ? "null"
                                 : left.indentedStr(indent + 5)) + ",\n" +
                   // Line 2
                   spaces(indent + 5) + i + ",\n" +

                   // Line 3
                   spaces(indent + 5) +
                   (right == null ? "null"
                                  : right.indentedStr(indent + 5)) +
                   ")";
        }
    }

    // Here's an example of how to make a data structure visible using a very simple binary tree.
    // The "Node" class is defined above.  Now the data structure looks like itself - and compiles to itself!
    // You could add the word, "new" if you wanted to make it homoiconic, but brevity adds clarity.
    @Test public void testNode() {
        assertEquals("Node(Node(Node(1),\n" +
                     "          2,\n" +
                     "          Node(3)),\n" +
                     "     4,\n" +
                     "     Node(Node(5),\n" +
                     "          6,\n" +
                     "          Node(7)))",
                     new Node(new Node(new Node(1),
                                       2,
                                       new Node(3)),
                              4,
                              new Node(new Node(5),
                                       6,
                                       new Node(7))).indentedStr(0));

        assertEquals("Node(Node(null,\n" +
                     "          1,\n" +
                     "          Node(2)),\n" +
                     "     3,\n" +
                     "     Node(Node(4),\n" +
                     "          5,\n" +
                     "          null))",
                     new Node(new Node(null,
                                       1,
                                       new Node(2)),
                              3,
                              new Node(new Node(4),
                                       5,
                                       null)).indentedStr(0));
    }

    @Test public void testQuestion() {
        assertEquals("\"\\u0024\"", stringify("$"));
        assertEquals("'\\u0024'", charToStr('$'));
    }

    @Test public void testNullWhenJ() {
        assertNotNull(nullWhen(2, 3));
        assertNull(nullWhen(2, 2));

        assertNotNull(nullWhen("a", "b"));
        assertNull(nullWhen("hi", "hi"));

        assertNotNull(nullWhen("x", s -> s.equals("y")));
        assertNull(nullWhen("x", s -> s.equals("x")));

        assertNotNull(nullWhen(2, n -> n > 2));
        assertNull(nullWhen(2, n -> n > 1));
    }
}

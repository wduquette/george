package com.wjduquette.george.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StringResultTest {
    @Test
    public void testResult_present() {
        StringResult good = new StringResult("abc");
        assertTrue(good.isPresent());
        assertFalse(good.isEmpty());
        assertEquals("abc", good.asIs());

        assertEquals("abc", good.or("xyz"));
        assertEquals("ABC", good.as(String::toUpperCase));
        assertEquals("ABC", good.asOr(String::toUpperCase, "XYZ"));

        assertThrows(IllegalArgumentException.class, good::asInt);
        assertThrows(IllegalArgumentException.class,
            () -> good.orInt(0));
        assertThrows(IllegalArgumentException.class, good::asDouble);
        assertThrows(IllegalArgumentException.class,
            () -> good.orDouble(0.0));
    }

    @Test
    public void testResult_empty() {
        StringResult bad = new StringResult(null);
        assertThrows(IllegalArgumentException.class, bad::asIs);
        assertFalse(bad.isPresent());
        assertTrue(bad.isEmpty());

        assertEquals("xyz", bad.or("xyz"));
        assertThrows(IllegalArgumentException.class,
            () -> bad.as(String::toUpperCase));
        assertEquals("XYZ", bad.asOr(String::toUpperCase, "XYZ"));

        assertThrows(IllegalArgumentException.class, bad::asInt);
        assertEquals(0, bad.orInt(0));
        assertThrows(IllegalArgumentException.class, bad::asDouble);
        assertEquals(0.0, bad.orDouble(0.0));
    }

    @Test
    public void testResult_int() {
        StringResult good = new StringResult("123");
        assertEquals(123, good.asInt());
    }

    @Test
    public void testResult_double() {
        StringResult good = new StringResult("123.4");
        assertEquals(123.4, good.asDouble());
    }

    @Test
    public void testResult_enum() {
        StringResult good = new StringResult("RED");
        assertEquals(Color.RED, good.as(Color::valueOf));

        StringResult syntax = new StringResult("VIOLET");
        assertThrows(IllegalArgumentException.class,
            () -> syntax.as(Color::valueOf));

        StringResult empty = new StringResult(null);
        assertThrows(IllegalArgumentException.class,
            () -> empty.as(Color::valueOf));
        assertEquals(Color.GREEN,
            empty.asOr(Color::valueOf, Color.GREEN));
    }

    public enum Color { RED, GREEN }
}

package com.wjduquette.george.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ResultTest {
    @Test
    public void testResult_present() {
        Result good = new Result("abc");
        assertTrue(good.isPresent());
        assertFalse(good.isEmpty());
        assertEquals("abc", good.value());

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
        Result bad = new Result(null);
        assertThrows(IllegalArgumentException.class, bad::value);
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
        Result good = new Result("123");
        assertEquals(123, good.asInt());
    }

    @Test
    public void testResult_double() {
        Result good = new Result("123.4");
        assertEquals(123.4, good.asDouble());
    }

    @Test
    public void testResult_enum() {
        Result good = new Result("RED");
        assertEquals(Color.RED, good.as(Color::valueOf));

        Result syntax = new Result("VIOLET");
        assertThrows(IllegalArgumentException.class,
            () -> syntax.as(Color::valueOf));

        Result empty = new Result(null);
        assertThrows(IllegalArgumentException.class,
            () -> empty.as(Color::valueOf));
        assertEquals(Color.GREEN,
            empty.asOr(Color::valueOf, Color.GREEN));
    }

    public enum Color { RED, GREEN }
}

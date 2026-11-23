package io.github.hero_premium.tools;

import io.github.hero_premium.util.Util;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PropertyTypeTest {

    @Test
    void stringToSTRING() {
        PropertyType type = Util.gson.fromJson("\"string\"", PropertyType.class);
        assertSame(String.class, type.getType());
    }

    @Test
    void stringToARRAY() {
        PropertyType type = Util.gson.fromJson("\"array\"", PropertyType.class);
        assertSame(List.class, type.getType());
    }

    @Test
    void stringToNUMBER() {
        PropertyType type = Util.gson.fromJson("\"number\"", PropertyType.class);
        assertSame(Double.class, type.getType());
    }

    @Test
    void stringToINTEGER() {
        PropertyType type = Util.gson.fromJson("\"integer\"", PropertyType.class);
        assertSame(Double.class, type.getType());
    }

    @Test
    void stringToOBJECT() {
        PropertyType type = Util.gson.fromJson("\"object\"", PropertyType.class);
        assertSame(Map.class, type.getType());
    }

    @Test
    void stringToBOOLEAN() {
        PropertyType type = Util.gson.fromJson("\"boolean\"", PropertyType.class);
        assertSame(Boolean.class, type.getType());
    }

    @Test
    void stringToNULL() {
        PropertyType type = Util.gson.fromJson("\"null\"", PropertyType.class);
        assertNull(type.getType());
    }

    @Test
    void passANumber() {
        Object num = Util.gson.fromJson("5", Object.class);
        assertTrue(PropertyType.INTEGER.compareType(num));
    }

    @Test
    void passANumber2() {
        Object num = Util.gson.fromJson("5.5", Object.class);
        assertTrue(PropertyType.NUMBER.compareType(num));
    }

}
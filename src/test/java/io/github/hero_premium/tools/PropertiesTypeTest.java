package io.github.hero_premium.tools;

import io.github.hero_premium.util.Util;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PropertiesTypeTest {

    @Test
    void stringToSTRING() {
        PropertiesType type = Util.gson.fromJson("\"string\"", PropertiesType.class);
        assertSame(String.class, type.getType());
    }

    @Test
    void stringToARRAY() {
        PropertiesType type = Util.gson.fromJson("\"array\"", PropertiesType.class);
        assertSame(List.class, type.getType());
    }

    @Test
    void stringToNUMBER() {
        PropertiesType type = Util.gson.fromJson("\"number\"", PropertiesType.class);
        assertSame(Double.class, type.getType());
    }

    @Test
    void stringToINTEGER() {
        PropertiesType type = Util.gson.fromJson("\"integer\"", PropertiesType.class);
        assertSame(Double.class, type.getType());
    }

    @Test
    void stringToOBJECT() {
        PropertiesType type = Util.gson.fromJson("\"object\"", PropertiesType.class);
        assertSame(Map.class, type.getType());
    }

    @Test
    void stringToBOOLEAN() {
        PropertiesType type = Util.gson.fromJson("\"boolean\"", PropertiesType.class);
        assertSame(Boolean.class, type.getType());
    }

    @Test
    void stringToNULL() {
        PropertiesType type = Util.gson.fromJson("\"null\"", PropertiesType.class);
        assertNull(type.getType());
    }

    @Test
    void passANumber() {
        Object num = Util.gson.fromJson("5", Object.class);
        assertTrue(PropertiesType.INTEGER.compareType(num));
    }

    @Test
    void passANumber2() {
        Object num = Util.gson.fromJson("5.5", Object.class);
        assertTrue(PropertiesType.NUMBER.compareType(num));
    }

}
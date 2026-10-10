package org.hero.tools;

import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ToolTest {

    @Test
    void toolUse() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Tool tool = new Tool("", ""
                , new Tool.Param("needed", PropertiesType.STRING, "needed very", true)
                ,new Tool.Param("not needed", PropertiesType.STRING, "not needed very", false) ) {
            @Override
            protected String useTool(Map<String, Object> arguments) {
                return "TEST PASSED!";
            }
        };
        Method method = Tool.class.getDeclaredMethod("toolUse", Map.class);
        method.setAccessible(true);


        String result = (String) method.invoke(tool, (Object) null);
        assertEquals("TOOL ERROR, NULL BEEN PASSED", result);

        result = (String) method.invoke(tool, Map.of());
        assertEquals("TOOL ERROR, MISSING A REQUIRED PARAMETER", result);

        result = (String) method.invoke(tool, Map.of("needed", "HI"));
        assertEquals("TEST PASSED!", result);

        result = (String) method.invoke(tool, Map.of("not needed", "very"));
        assertEquals("TOOL ERROR, MISSING A REQUIRED PARAMETER", result);

        result = (String) method.invoke(tool, Map.of("needed", "hi", "not needed", "very"));
        assertEquals("TEST PASSED!", result);

        result = (String) method.invoke(tool, Map.of("needed", "hi", "not needed", true));
        assertEquals("TOOL ERROR, EXPECTED STRING, RECEIVED BOOLEAN", result);

        tool = new Tool("", "") {
            @Override
            protected String useTool(Map<String, Object> arguments) {
                return "TEST PASSED!";
            }
        };

        // too many params
        result = (String) method.invoke(tool, Map.of("needed", "hi", "not needed", true));
        assertEquals("TEST PASSED!", result);
    }
}
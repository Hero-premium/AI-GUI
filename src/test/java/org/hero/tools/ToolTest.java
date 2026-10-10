package org.hero.tools;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ToolTest {

    @Test
    void toolUse() {
        Tool tool = new Tool("", ""
                , new Tool.Param("needed", PropertiesType.STRING, "needed very", true)
                , new Tool.Param("not needed", PropertiesType.STRING, "not needed very", false)) {
            @Override
            protected String useTool(Map<String, Object> arguments) {
                return "TEST PASSED!";
            }
        };

        String result;

        result = tool.toolUse(null);
        assertEquals("TOOL ERROR, NULL BEEN PASSED", result);

        result = tool.toolUse(Map.of());
        assertEquals("TOOL ERROR, MISSING A REQUIRED PARAMETER", result);

        result = tool.toolUse(Map.of("needed", "HI"));
        assertEquals("TEST PASSED!", result);

        result = tool.toolUse(Map.of("not needed", "very"));
        assertEquals("TOOL ERROR, MISSING A REQUIRED PARAMETER", result);

        result = tool.toolUse(Map.of("needed", "hi", "not needed", "very"));
        assertEquals("TEST PASSED!", result);

        result = tool.toolUse(Map.of("needed", "hi", "not needed", true));
        assertEquals("TOOL ERROR, EXPECTED STRING, RECEIVED BOOLEAN", result);

        tool = new Tool("", "") {
            @Override
            protected String useTool(Map<String, Object> arguments) {
                return "TEST PASSED!";
            }
        };

        // too many params
        result = tool.toolUse(Map.of("needed", "hi", "not needed", true));
        assertEquals("TEST PASSED!", result);
    }
}
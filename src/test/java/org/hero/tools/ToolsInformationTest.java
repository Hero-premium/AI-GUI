package org.hero.tools;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ToolsInformationTest {

    @Test
    void createBuilder() {
        assertNotNull(ToolsInformation.ToolDataBuilder.builder());
    }

    @Test
    void builderEarlyCall() {
        Executable executable = () -> ToolsInformation.ToolDataBuilder.builder().build();
        assertThrows(IllegalStateException.class, executable);
    }

    @Test
    void earlyFunctionCall() {
        Executable executable = () -> ToolsInformation.ToolDataBuilder.builder().function("null", "null").build();
        assertThrows(IllegalStateException.class, executable);
    }


    @Test
    void workingBuilder() {
        ToolsInformation.ToolData tool = ToolsInformation.ToolDataBuilder.builder()
                .parameter("hello", PropertiesType.STRING, "kill the wither boss with it", true)
                .buildParameters()
                .function("a sword", "very powerful")
                .build();
        assertNotNull(tool);
    }

    @Test
    void calledWithNulls() {
        Executable executable = () -> ToolsInformation.ToolDataBuilder.builder()
                .parameter("hello", PropertiesType.STRING, "kill the wither boss with it", true)
                .buildParameters()
                .function("a sword", null)
                .build();
        assertThrows(NullPointerException.class, executable);
    }
}
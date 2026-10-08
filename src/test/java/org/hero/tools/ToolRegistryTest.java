package org.hero.tools;

import org.hero.Requests;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ToolRegistryTest {

    @Test
    void getTool() {
        Optional<Tool> tool = ToolRegistry.getTool("AToolThatDoesntExist");
        assertFalse(tool.isPresent());
        tool = ToolRegistry.getTool("CurrentTime");
        assertTrue(tool.isPresent());
    }

    @Test
    void findAndRunTools() {
        List<Requests.Message> messageList = ToolRegistry.findAndRunTools(null);
        assertTrue(messageList.isEmpty());
        messageList = ToolRegistry.findAndRunTools(List.of(
                new ToolsInformation.ToolCall("call_1",
                new ToolsInformation.FunctionCall(0, "ToolThatDoesn'tExist", Map.of("text", "hello")))));
        assertEquals("unknown tool", messageList.getFirst().content());
    }
}
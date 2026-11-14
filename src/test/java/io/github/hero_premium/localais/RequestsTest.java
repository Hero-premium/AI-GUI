package io.github.hero_premium.localais;

import io.github.hero_premium.Requests;
import io.github.hero_premium.Roles;
import io.github.hero_premium.tools.ToolsInformation;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RequestsTest {
    @Test
    void getEvalDurationInSeconds() {
        int result = new Requests.RequestOut("system", "",
                new Requests.Message(Roles.USER_FACING, "ollama server is down"),
                true, "", 0, 0,
                0, 0, 0,
                0, 5_000_000_000L).getEvalDurationInSeconds();
        assertEquals(5, result);
    }

    @Test
    void noMessage() {
        Requests.Message message = new Requests.Message(Roles.USER_FACING, null, null, null, null);
        assertTrue(message.noContentOrToolCalls());
    }
    @Test
    void contentButNoToolCalls(){
        Requests.Message message = new Requests.Message(Roles.USER_FACING, "hi", null, null, null);
        assertFalse(message.noContentOrToolCalls());
    }

    @Test
    void noContentButWithToolCalls(){
        Requests.Message message = new Requests.Message(Roles.USER_FACING, null, null, List.of(new ToolsInformation.ToolCall("", null)), null);
        assertFalse(message.noContentOrToolCalls());
    }

    @Test
    void noContentButNoToolCalls(){
        Requests.Message message = new Requests.Message(Roles.USER_FACING, "", null, List.of(), null);
        assertTrue(message.noContentOrToolCalls());
    }
}
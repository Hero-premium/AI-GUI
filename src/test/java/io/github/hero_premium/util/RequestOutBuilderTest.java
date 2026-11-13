package io.github.hero_premium.util;

import io.github.hero_premium.Requests;
import io.github.hero_premium.Roles;
import io.github.hero_premium.tools.ToolsInformation;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class RequestOutBuilderTest {

    @Contract("_, _ -> new")
    private static Requests.@NonNull RequestOut putDefaults(String message, boolean isDone) {
        return putDefaults(message, isDone, (List<ToolsInformation.ToolCall>) null);
    }

    private static Requests.@NonNull RequestOut putDefaults(String message, boolean isDone, List<ToolsInformation.ToolCall> toolCalls) {
        return putDefaults(message, isDone, toolCalls, Roles.ASSISTANT);
    }

    @Contract("_, _, _ -> new")
    private static Requests.@NonNull RequestOut putDefaults(String message, boolean isDone, Roles sayer) {
        return putDefaults(message, isDone, null, sayer);
    }

    private static Requests.@NonNull RequestOut putDefaults(String message, boolean isDone, List<ToolsInformation.ToolCall> toolCalls, Roles sayer) {
        return new Requests.RequestOut(
                "fakeAi", "now",
                new Requests.Message(sayer, message, null, toolCalls, null), isDone, isDone ? "finished" : null,
                1, 1, 1, 1, 1, 1, 1);
    }

    @Test
    void basicUse() {
        RequestOutBuilder connector = new RequestOutBuilder();
        List<Requests.RequestOut> requestOuts = List.of(
                putDefaults("h", false),
                putDefaults("e", false),
                putDefaults("l", false),
                putDefaults("l", false),
                putDefaults("o", true));
        String result = null;

        for (Requests.RequestOut requestOut : requestOuts) {
            Requests.RequestOut request = connector.appendMessages(requestOut);
            if (request != null) result = request.message().content();
        }
        assertEquals("hello", result);
    }

    @Test
    void resultBeforeFinish() {
        RequestOutBuilder connector = new RequestOutBuilder();
        Requests.RequestOut request = connector.appendMessages(putDefaults("h", false));
        assertNull(request);
    }

    @Test
    void withAToolCall() {
        RequestOutBuilder connector = new RequestOutBuilder();
        final List<ToolsInformation.ToolCall> toolcall = List.of(
                new ToolsInformation.ToolCall("2",
                        new ToolsInformation.FunctionCall(1, "calculator", Map.of()))
        );
        List<Requests.RequestOut> requestOuts = List.of(
                putDefaults("h", false),
                putDefaults("e", false),
                putDefaults("l", false),
                putDefaults("l", false),
                putDefaults("o", true, toolcall
                ));

        String result = null;
        List<ToolsInformation.ToolCall> toolCalls = List.of();
        for (Requests.RequestOut requestOut : requestOuts) {
            Requests.RequestOut request = connector.appendMessages(requestOut);
            if (request != null) {
                result = request.message().content();
                toolCalls = request.message().tool_calls();
            }
        }
        assertEquals("hello", result);
        assertEquals(toolcall, toolCalls);
    }
@Test
    void reuse() {
        RequestOutBuilder connector = new RequestOutBuilder();
        String one = connector.appendMessages(putDefaults("h", true)).message().content();
        String two = connector.appendMessages(putDefaults("e", true)).message().content();
        assertEquals("h", one);
        assertEquals("e", two);
    }

    @Test
    void reuseWithTools() {
        RequestOutBuilder connector = new RequestOutBuilder();
        final List<ToolsInformation.ToolCall> toolcall1 = List.of(
                new ToolsInformation.ToolCall("2",
                        new ToolsInformation.FunctionCall(1, "calculator", Map.of()))
        );
        final List<ToolsInformation.ToolCall> toolcall2 = List.of(
                new ToolsInformation.ToolCall("2",
                        new ToolsInformation.FunctionCall(3, "calculatorors", Map.of()))
        );

        Requests.RequestOut request1 = connector.appendMessages(putDefaults("h", true, toolcall1));
        String one = request1.message().content();
        var toolscalls1 = request1.message().tool_calls();

        Requests.RequestOut request2 = connector.appendMessages(putDefaults("e", true, toolcall2));
        String two = request2.message().content();
        var toolscalls2 = request2.message().tool_calls();

        assertEquals("h", one);
        assertEquals(toolcall1, toolscalls1);

        assertEquals("e", two);
        assertEquals(toolcall2, toolscalls2);
    }


}
package io.github.hero_premium.program;

import io.github.hero_premium.Requests;
import io.github.hero_premium.Roles;
import io.github.hero_premium.localais.LocalAi;
import io.github.hero_premium.clients.Client;
import io.github.hero_premium.tools.ToolsInformation;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class ProgramTest {

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
    void normalChat() {
        List<String> messages = new ArrayList<>();
        Program program = new Program(new FakeClient(messages), new FakeAi(Stream.of(
                putDefaults("h", false),
                putDefaults("e", false),
                putDefaults("l", false),
                putDefaults("l", false),
                putDefaults("o", true)
        )));
        program.chat("hi");

        char[] litters = new char[]{
                'h',
                'e',
                'l',
                'l',
                'o'
        };

        // not the intended behavior but none of Program's business
        for (int i = 0; i < litters.length; i++) {
            // skip system prompt
            assertEquals(litters.length, messages.size() - 1);
            assertEquals(litters[i], messages.get(i + 1).charAt(0));
        }
    }

    @Test
    void toolCall() {
        List<String> messages = new ArrayList<>();
        Program program = new Program(new FakeClient(messages), new FakeAi(Stream.of(
                putDefaults(null, false, List.of(
                                new ToolsInformation.ToolCall("2",
                                        new ToolsInformation.FunctionCall(1, "calculator", Map.of()))
                        )
                ),
                putDefaults("hellos", false, Roles.TOOL),
                putDefaults("h", false),
                putDefaults("e", false),
                putDefaults("l", false),
                putDefaults("l", false),
                putDefaults("o", true)
        )));

        program.chat("hi");
        IO.println(messages.toString());
    }

    @Test
    void toolLoop() {
        List<String> messages = new ArrayList<>();
        Program program = new Program(new FakeClient(messages), new FakeAi(Stream.of(
                putDefaults(null, false, List.of(
                                new ToolsInformation.ToolCall("2",
                                        new ToolsInformation.FunctionCall(1, "calculator", Map.of()))
                        )
                ))));
        program.chat("hello");
        assertSame("", messages.get(0));
        assertSame("unknown tool", messages.get(1));
    }

    private static class FakeAi extends LocalAi {

        private final List<Requests.RequestOut> toSay;

        protected FakeAi(@NonNull Stream<Requests.RequestOut> toSay) {
            super("fakeAi");
            this.toSay = toSay.toList();
        }

        @Contract(pure = true)
        @Override
        public void chat(@NonNull List<Requests.Message> messages, Consumer<Requests.RequestOut> out) {
             toSay.getFirst();
        }
    }

    private static class FakeClient extends Client {
        //TODO make this compatible with streams
        private final List<String> messages;

        private FakeClient(@NotNull List<String> messages) {
            this.messages = messages;
        }

        @Override
        public void launchApplication() {

        }

        @Override
        public void displayUserMessage(String message) {
            messages.add(message);
        }

        @Override
        public void displayToolsMessage(String message) {
            messages.add(message);
        }

        @Override
        public void displayAIMessage(String message) {
            messages.add(message);
        }

        @Override
        public void displaySystemMessage(String message) {
            messages.add(message);
        }

        @Override
        public void endLine() {

        }
    }

}
package io.github.hero_premium.program;

import io.github.hero_premium.Requests.Message;
import io.github.hero_premium.Requests.RequestOut;
import io.github.hero_premium.clients.Client;
import io.github.hero_premium.localais.LocalAi;
import io.github.hero_premium.program.conversations.Conversation;
import io.github.hero_premium.tools.ToolRegistry;
import io.github.hero_premium.tools.ToolsInformation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
// fixme there's a fishy bug here, ig
class ToolHandler {

    private static final int MAX_TOOLS_REQUESTS = 5;
    private final List<Message> toolReplies = new CopyOnWriteArrayList<>();
    private final AtomicInteger toolLoops = new AtomicInteger();

    @Nullable RequestOut handleTools(
            @NotNull RequestOut requestOut,
            @NotNull Conversation conversation,
            @NotNull LocalAi ai,
            @NotNull Client client,
            @NotNull BiConsumer<RequestOut, Conversation> chatHandler) {

        requireNonNull(requestOut, conversation, ai, client, chatHandler);

        List<ToolsInformation.ToolCall> toolCalls = requestOut.message().tool_calls();
        if (hasToolCall(toolCalls)) {
            toolLoops.incrementAndGet();
            if (toolLoops.get() >= MAX_TOOLS_REQUESTS) {
                toolLoops.set(0);
                ai.interrupt();
                return RequestOut.failureDefaultsIn(ai.modelName,
                        "the AI hit a limit and couldn't generate a response, please try again");
            }
        }

        List<Message> toolResults = ToolRegistry.findAndRunTools(toolCalls);
        toolReplies.addAll(toolResults);
        displayAsTool(toolResults, client);

        if (requestOut.done() && !toolReplies.isEmpty()) {
            drainToolReplies(conversation, ai, chatHandler);
            return null;
        }

        String content = requestOut.message().content();
        if (requestOut.done() || content != null && !content.isEmpty()) {
            toolLoops.set(0);
            return requestOut;
        }
        return null;
    }

    private void drainToolReplies(@NotNull Conversation conversation, @NotNull LocalAi ai, @NotNull BiConsumer<RequestOut, Conversation> chatHandler) {
        List<Message> copy = List.copyOf(toolReplies);
        toolReplies.clear();
        conversation.chat(copy, ai, requestOut -> chatHandler.accept(requestOut, conversation));
    }

    private static void requireNonNull(@NotNull RequestOut requestOut, @NotNull Conversation conversation, @NotNull LocalAi ai, @NotNull Client client, @NotNull BiConsumer<RequestOut, Conversation> chatHandler) {
        Objects.requireNonNull(requestOut, "requestOut must not be null");
        Objects.requireNonNull(conversation, "conversation must not be null");
        Objects.requireNonNull(client, "client must not be null");
        Objects.requireNonNull(chatHandler, "chatHandler must not be null");
        Objects.requireNonNull(ai, "ai must not be null");
    }

    private static boolean hasToolCall(@Nullable List<ToolsInformation.ToolCall> toolCalls) {
        return toolCalls != null && !toolCalls.isEmpty();
    }

    private static void displayAsTool(@NotNull Collection<Message> toolReplies, @NotNull Client client) {
        toolReplies.stream()
                .map(toolCalls -> toolCalls.tool_name() + " " + toolCalls.content())
                .forEach(client::displayToolsMessage);
    }
}

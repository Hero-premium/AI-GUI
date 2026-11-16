package io.github.hero_premium.program;

import io.github.hero_premium.Requests.Message;
import io.github.hero_premium.Requests.RequestOut;
import io.github.hero_premium.Roles;
import io.github.hero_premium.clients.Client;
import io.github.hero_premium.commands.CommandsRegistry;
import io.github.hero_premium.localais.Llama3b;
import io.github.hero_premium.localais.LocalAi;
import io.github.hero_premium.localais.LocalAiRegistry;
import io.github.hero_premium.program.conversations.Conversation;
import io.github.hero_premium.program.conversations.ConversationsManager;
import io.github.hero_premium.tools.ToolRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

public class Program {

    private static final int MAX_TOOLS_REQUESTS = 5;
    private static final Logger LOGGER = LoggerFactory.getLogger(Program.class);
    @NotNull
    private final ConversationsManager conversationsManager = new ConversationsManager();
    @NotNull
    private final Client client;
    @NotNull
    private LocalAi ai;

    public void setAi(LocalAi ai) {
        this.ai = Objects.requireNonNull(ai, "ai must not be null");
    }

    public Program(@NotNull Client client, LocalAi ai) {
        Objects.requireNonNull(client, "client must not be null");
        Client varClient = Objects.requireNonNull(client.getClient(), "client.getClient() must not return null");
        this.client = Objects.requireNonNull(varClient.setProgram(this), "client.setProgram(Program) must not return null");
        this.ai = Objects.requireNonNull(ai, "ai must not be null");
    }

    public Program(@NotNull Client client) {
        this(client, new Llama3b());
    }

    public void launch() {
        client.launchApplication();
    }

    /**
     * call this to chat with the AI, it handles tools and commands and queue
     *
     * @param request the String of what the user wants to tell the AI
     */
    public void chat(@NotNull String request) {
        if (request.isBlank()) return;
        client.displayUserMessage(request);
        Conversation conversation = conversationsManager.getCurrentConversation();
        if (handleCommands(request, conversation)) return;

        conversation.chat(List.of(new Message(Roles.USER, request)), ai, requestOut -> chatHandler(requestOut, conversation));

        while (conversation.isQueued()) {
            Message next = conversation.getNext();
            displayMessage(next);
            conversation.chat(List.of(next), ai, requestOut -> chatHandler(requestOut, conversation));
        }
    }

    private void chatHandler(@NotNull RequestOut requestOut, Conversation conversation) {
        requestOut = handleTools(requestOut, conversation);
        if (requestOut == null) return;
        displayMessage(requestOut.message());
        if (requestOut.done()) client.endLine();
    }

    private final List<Message> toolReplies = new CopyOnWriteArrayList<>();

    @Nullable RequestOut handleTools(@NotNull RequestOut requestOut, final Conversation conversation) {
        Objects.requireNonNull(requestOut, "requestOut must not be null");
        RequestOut[] requestOuts = new RequestOut[]{requestOut};
        // todo bring back the max limit + fix bugs
        toolReplies.addAll(ToolRegistry.findAndRunTools(requestOut.message().tool_calls()));

        if (requestOuts[0].done() && !toolReplies.isEmpty()) {
            List<Message> copy = List.copyOf(toolReplies);
            toolReplies.clear();
            displayAsTool(copy.stream());
            conversation.chat(copy, ai, requestOut1 -> {
                requestOuts[0] = requestOut1;
                chatHandler(requestOut1, conversation);
            });
            return null;
        }

        if (requestOuts[0].done()) return requestOuts[0];
        String content = requestOuts[0].message().content();
        return (content == null || content.isEmpty()) ? null : requestOuts[0];

    }

    private void displayAsTool(@NotNull Stream<Message> toolReplies) {
        toolReplies
                .map(toolCalls -> toolCalls.tool_name() + " " + toolCalls.content())
                .forEach(client::displayToolsMessage);
    }

    private void displayMessage(@NotNull Message message) {
        String content = message.content();
        if (content == null) return;
        switch (message.role()) {
            case USER -> client.displayUserMessage(content);
            case TOOL -> client.displayToolsMessage(content);
            case ASSISTANT -> client.displayAIMessage(content);
            case SYSTEM, USER_FACING -> client.displaySystemMessage(content);
            default -> LOGGER.warn("switch in io.github.hero_premium.program.Program reached an unexpected case");
        }
    }

    private boolean handleCommands(@NotNull String request, Conversation conversation) {
        if (!request.startsWith("/")) return false;
        for (Message message : CommandsRegistry.findAndRunCommand(request, conversation, client)) {
            displayMessage(message);
        }
        return true;
    }

    public Map<String, LocalAi> getAllAis() {
        return LocalAiRegistry.AIS;
    }

}
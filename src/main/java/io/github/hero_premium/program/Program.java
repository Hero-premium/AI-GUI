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
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Objects;

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

        conversation.chat(List.of(new Message(Roles.USER, request)), ai, this::chatHandler);
        if (conversation.isQueued()) {
            conversation.chat(List.of(conversation.getNext()), ai, this::chatHandler);
        }
    }

    private void chatHandler(@NotNull RequestOut requestOut) {
        //requestOut = handleTools(requestOut, conversation);
        displayMessage(requestOut.message());
        if (requestOut.done()) client.endLine();
    }

//    @NotNull RequestOut handleTools(@NotNull RequestOut requestOut, Conversation conversation) {
//        Objects.requireNonNull(requestOut, "requestOut must not be null");
//        List<Message> toolReplies = ToolRegistry.findAndRunTools(requestOut.message().tool_calls());
//
//        int i = 0;
//        while (!toolReplies.isEmpty()) {
//            displayAsTool(toolReplies);
//
//            requestOut = conversation.pollLinesAndSaveThem(ai);
//            conversation.chat(toolReplies, ai);
//            toolReplies = ToolRegistry.findAndRunTools(requestOut.message().tool_calls());
//
//            requestOut = conversation.pollLinesAndSaveThem(ai);
//            i++;
//
//            if (i >= MAX_TOOLS_REQUESTS)
//                return RequestOut.defaultsIn(ai.modelName, "the AI hit tool calls limit and couldn't generate a response, please try again");
//        }
//        return requestOut;
//    }
//
//    private void displayAsTool(@NotNull List<Message> toolReplies) {
//        toolReplies.stream()
//                .map(toolCalls -> toolCalls.tool_name() + " " + toolCalls.content())
//                .forEach(client::displayToolsMessage);
//    }

    private void displayMessage(@NotNull Message message) {
        switch (message.role()) {
            case USER -> client.displayUserMessage(message.content());
            case TOOL -> client.displayToolsMessage(message.content());
            case ASSISTANT -> client.displayAIMessage(message.content());
            case SYSTEM, USER_FACING -> client.displaySystemMessage(message.content());
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
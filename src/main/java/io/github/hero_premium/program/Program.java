package io.github.hero_premium.program;

import io.github.hero_premium.Requests.Message;
import io.github.hero_premium.Requests.RequestOut;
import io.github.hero_premium.Roles;
import io.github.hero_premium.clients.Client;
import io.github.hero_premium.commands.CommandsRegistry;
import io.github.hero_premium.localais.LocalAi;
import io.github.hero_premium.localais.LocalAiRegistry;
import io.github.hero_premium.program.conversations.Conversation;
import io.github.hero_premium.program.conversations.ConversationsManager;
import io.github.hero_premium.program.sqlite.Constants;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;

public class Program {


    private static final Logger LOGGER = LoggerFactory.getLogger(Program.class);
    @NotNull
    private final ConversationsManager conversationsManager;
    @NotNull
    private final ToolHandler toolHandler = new ToolHandler();
    @NotNull
    private final ThinkingDisplayHandler thinkingDisplayHandler = new ThinkingDisplayHandler();
    @NotNull
    private final Client client;
    @NotNull
    private volatile LocalAi ai;

    // todo add fourth overload
    public Program(@NotNull Client client, @NotNull LocalAi ai) {
        this(client, ai, Constants.DEFAULT_URL);
    }

    public Program(@NotNull Client client) {
        this(client, LocalAiRegistry.AIS.get("qwen3:4b"));
    public Program(@NotNull Client client, @NotNull LocalAi ai, String sqlUrl) {
        conversationsManager = new ConversationsManager(sqlUrl);
    }

    public void launch() {
        client.launchApplication();
    }

    /**
     * call this to chat with the AI, it handles tools and commands and queue
     *
     * @param message the message of what to tell the AI
     */
    public void chat(@NotNull Message message) {
        String content = message.content();
        if (content == null || content.isBlank()) return;
        displayMessage(message);

        Conversation conversation = conversationsManager.getCurrentConversation();
        if (handleCommands(content, conversation)) return;

        conversation.chat(message, ai, requestOut -> chatHandler(requestOut, conversation));

    }

    public void chat(String request) {
        chat(new Message(Roles.USER, request));
    }

    private void chatHandler(@NotNull RequestOut requestOut, Conversation conversation) {
        thinkingDisplayHandler.display(requestOut, client);
        requestOut = toolHandler.handleTools(
                requestOut,
                conversation,
                ai,
                client,
                this::chatHandler);
        if (requestOut == null) return;
        displayMessage(requestOut.message());
        if (requestOut.done()) {
            client.endLine();
            if (conversation.isQueued() && !ai.isThinking()) chat(conversation.getNext());
        }
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
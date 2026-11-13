package io.github.hero_premium.program;

import io.github.hero_premium.Requests;
import io.github.hero_premium.Requests.Message;
import io.github.hero_premium.Roles;
import io.github.hero_premium.localais.Llama3b;
import io.github.hero_premium.localais.LocalAi;
import io.github.hero_premium.localais.LocalAiRegistry;
import io.github.hero_premium.clients.Client;
import io.github.hero_premium.commands.CommandsRegistry;
import io.github.hero_premium.program.conversations.Conversation;
import io.github.hero_premium.program.conversations.ConversationsManager;
import io.github.hero_premium.tools.ToolRegistry;
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
    private final Client gui;
    @NotNull
    private LocalAi ai;

    public void setAi(LocalAi ai) {
        this.ai = Objects.requireNonNull(ai, "ai must not be null");
    }

    public Program(@NotNull Client client, LocalAi ai) {
        Objects.requireNonNull(client, "client must not be null");
        Client varClient = Objects.requireNonNull(client.getClient(), "client.getClient() must not return null");
        this.gui = Objects.requireNonNull(varClient.setProgram(this), "client.setProgram(Program) must not return null");
        this.ai = Objects.requireNonNull(ai, "ai must not be null");
    }

    public Program(@NotNull Client client) {
        this(client, new Llama3b());
    }

    public void launch() {
        gui.launchApplication();
    }

    /**
     * call this to chat with the AI, it handles tools and commands and queue
     *
     * @param request the String of what the user wants to tell the AI
     */
    public void chat(@NotNull String request) {
        if (request.isBlank()) return;
        gui.displayUserMessage(request);
        Conversation conversation = conversationsManager.getCurrentConversation();
        if (handleCommands(request, conversation)) return;

        Requests.RequestOut req = conversation.chat(List.of(new Message(Roles.USER, request)), ai);
        if (req == null) return;
        req = handleTools(req, conversation);
        if (req == null) return;
        displayMessage(req.message());
        if (conversation.isQueued()) {
            chat(conversation.getNext().content());
        }
    }

    private Requests.@Nullable RequestOut handleTools(Requests.@NotNull RequestOut req, Conversation conversation) {
        List<Message> toolReplies = ToolRegistry.findAndRunTools(req.message().tool_calls());

        for (int i = 0; i < MAX_TOOLS_REQUESTS; i++) {
            if (toolReplies.isEmpty()) break;
            for (Message toolCalls : toolReplies) {
                gui.displayToolsMessage(toolCalls.tool_name() + " " + toolCalls.content());
            }
            req = conversation.chat(toolReplies, ai);

            toolReplies = ToolRegistry.findAndRunTools(req.message().tool_calls());
        }
        if (!toolReplies.isEmpty()) {
            gui.displaySystemMessage("the AI hit a limit and couldn't generate a response, please try again");
            return null;
        }
        return req;
    }

    private void displayMessage(@NotNull Message message) {
        switch (message.role()) {
            case USER -> gui.displayUserMessage(message.content());
            case TOOL -> gui.displayToolsMessage(message.content());
            case ASSISTANT -> gui.displayAIMessage(message.content());
            case SYSTEM, USER_FACING -> gui.displaySystemMessage(message.content());
            default -> LOGGER.warn("switch in io.github.hero_premium.program.Program reached an unexpected case");
        }
    }

    private boolean handleCommands(@NotNull String request, Conversation conversation) {
        if (!request.startsWith("/")) return false;
        for (Message message : CommandsRegistry.findAndRunCommand(request, conversation, gui)) {
            gui.displayAIMessage(message.content());
            return true;
        }
        return false;
    }

    public Map<String, LocalAi> getAllAis() {
        return LocalAiRegistry.AIS;
    }

}
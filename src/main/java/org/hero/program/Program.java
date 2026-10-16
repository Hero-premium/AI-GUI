package org.hero.program;

import org.hero.Requests;
import org.hero.Requests.Message;
import org.hero.Roles;
import org.hero.chatai.Llama3b;
import org.hero.chatai.LocalAi;
import org.hero.chatai.LocalAiRegistry;
import org.hero.chatgui.Client;
import org.hero.commands.CommandsRegistry;
import org.hero.program.conversations.ConversationsManager;
import org.hero.program.conversations.QueueHelper;
import org.hero.tools.ToolRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Program {

    private static final int MAX_TOOLS_REQUESTS = 5;

    private final QueueHelper queueHelper = new QueueHelper();
    private final ConversationsManager conversationsManager = new ConversationsManager();
    private final Client gui;
    private LocalAi ai;

    public void setAi(LocalAi ai) {
        this.ai = Objects.requireNonNull(ai, "ai must not be null");
    }

    public Program(@NotNull Client client) {
        Objects.requireNonNull(client, "client must not be null");
        Client varClient = Objects.requireNonNull(client.getClient(), "client.getClient() must not return null");
        this.gui = Objects.requireNonNull(varClient.setProgram(this), "client.setProgram(Program) must not return null");
        this.ai = new Llama3b();
    }


    void launch() {
        gui.launchApplication();
    }

    /**
     * call this to chat with the AI, it handles tools and commands and queue
     *
     * @param request the String of what the user wants to tell the AI
     */
    public void chat(@NotNull String request) {
        if (request.isEmpty()) return;
        gui.displayUserMessage(request);
        if (handleCommands(request)) return;
        if (ai.isThinking()) {
            queueHelper.addToQueue(request);
            return;
        }
        Requests.RequestOut req = ai.chat(List.of(new Message(Roles.USER, request)));
        req = handleTools(req);
        if (req == null) return;
        displayMessage(req.message());
        if (queueHelper.isQueued()) {
            chat(queueHelper.getNext());
        }
    }

    private Requests.@Nullable RequestOut handleTools(Requests.@NotNull RequestOut req) {
        List<Message> toolReplies = ToolRegistry.findAndRunTools(req.message().tool_calls());

        for (int i = 0; i < MAX_TOOLS_REQUESTS; i++) {
            if (toolReplies.isEmpty()) break;
            for (Message toolCalls : toolReplies) {
                gui.displayToolsMessage(toolCalls.tool_name() + " " + toolCalls.content());
            }
            req = ai.chat(toolReplies);

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
            case SYSTEM -> gui.displaySystemMessage(message.content());
        }
    }

    private boolean handleCommands(String request) {
        if (CommandsRegistry.commandExists(request)) {
            for (Message message : CommandsRegistry.findAndRunCommand(request, ai, gui)) {
                gui.displayAIMessage(message.content());
            }
            return true;
        }
        return false;
    }

    public Map<String, LocalAi> getAllAis() {
        return LocalAiRegistry.AIS;
    }

}
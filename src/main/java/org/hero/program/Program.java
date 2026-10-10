package org.hero.program;

import org.hero.Requests;
import org.hero.Requests.Message;
import org.hero.Roles;
import org.hero.chatai.Llama3b;
import org.hero.chatai.LocalAi;
import org.hero.chatgui.Client;
import org.hero.commands.CommandsRegistry;
import org.hero.tools.ToolRegistry;

import java.util.List;

public class Program {

    private static final int MAX_TOOLS_REQUESTS = 5;

    private final QueueHelper queueHelper = new QueueHelper();

    private LocalAi ai;
    private Client gui;


    public Program(Client client) {
        this.gui = client.setProgram(this);
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
    public void chat(String request) {
        gui.displayUserMessage(request);
        if (handleCommands(request)) return;
        if (ai.isThinking()) {
            queueHelper.addToQueue(request);
            return;
        }
        Requests.RequestOut req = handleTools(request);
        if (req == null) return;
        displayMessage(req.message());
        if (queueHelper.isQueued()) {
            chat(queueHelper.getNext());
        }
    }

    private Requests.RequestOut handleTools(String request) {
        Requests.RequestOut req = ai.chat(List.of(new Message(Roles.USER, request)));
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

    private void displayMessage(Message message) {
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
}
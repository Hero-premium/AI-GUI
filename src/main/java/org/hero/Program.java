package org.hero;

import org.hero.Requests.Message;
import org.hero.chatai.Llama3b;
import org.hero.chatai.LocalAi;
import org.hero.chatgui.Client;
import org.hero.chatgui.javafx.JavaFX;
import org.hero.commands.CommandsRegistry;
import org.hero.tools.ToolRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Program {
    private static final Logger LOGGER = LoggerFactory.getLogger(Program.class);

    private static LocalAi ai;
    private static Client gui;
    private static final int MAX_TOOLS_REQUESTS = 5;

    public Program() {
        JavaFX.JavaFXLauncher.launch();
        gui = JavaFX.JavaFXLauncher.getJavaFX();
        ai = new Llama3b();
    }

    public void launch() {
        gui.launchApplication();
    }

    public static void chat(String request) {
        gui.displayUserMessage(request);
        if (handleCommands(request)) return;
        Requests.RequestOut req = handleTools(request);
        if (req == null) return;
        displayMessage(req);
    }

    private static Requests.RequestOut handleTools(String request) {
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

    private static void displayMessage(Requests.RequestOut req) {
        switch (req.message().role()) {
            case USER -> gui.displayUserMessage(req.message().content());
            case TOOL -> gui.displayToolsMessage(req.message().content());
            case ASSISTANT -> gui.displayAIMessage(req.message().content());
            case SYSTEM -> gui.displaySystemMessage(req.message().content());
        }
    }

    private static boolean handleCommands(String request) {
        if (CommandsRegistry.commandExists(request)) {
            for (Message message : CommandsRegistry.findAndRunCommand(request, ai, gui)) {
                gui.displayAIMessage(message.content());
            }
            return true;
        }
        return false;
    }
}
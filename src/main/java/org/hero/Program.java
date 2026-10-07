package org.hero;

import org.hero.chatai.Llama3b;
import org.hero.chatai.LocalAi;
import org.hero.chatgui.Client;
import org.hero.chatgui.javafx.JavaFX;
import org.hero.commands.CommandsRegistry;
import org.hero.tools.ToolRegistry;

import java.util.List;

public class Program {

    private static LocalAi ai;
    private static Client gui;
    private static final int MAX_TOOLS_REQUESTS = 5;

    public Program() {
        gui = new JavaFX();
        ai = new Llama3b();
    }

    public static List<Requests.Message> chat(String request) {
        if (CommandsRegistry.commandExists(request)) return CommandsRegistry.findAndRunCommand(request, ai, gui);

        Requests.RequestOut req = ai.chat(List.of(new Requests.Message(Roles.USER, request)));
        List<Requests.Message> toolReplies = ToolRegistry.findAndRunTools(req.message().tool_calls());

        for (int i = 0; i < MAX_TOOLS_REQUESTS; i++) {
            if (toolReplies.isEmpty()) break;
            for (Requests.Message toolCalls : toolReplies) {
                gui.toolsDisplay(toolCalls.tool_name() + " " + toolCalls.content());
            }
            req = ai.chat(toolReplies);

            toolReplies = ToolRegistry.findAndRunTools(req.message().tool_calls());
        }
        if (!toolReplies.isEmpty())
            return List.of(new Requests.Message(Roles.SYSTEM, "the AI hit a limit and couldn't generate a response, please try again"));
        return List.of(req.message());
    }

    public void launch() {
        gui.launchApplication();
    }
}

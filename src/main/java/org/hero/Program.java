package org.hero;

import org.hero.chatai.Llama3b;
import org.hero.chatai.LocalAi;
import org.hero.chatgui.Client;
import org.hero.chatgui.ScannerInput;
import org.hero.commands.CommandsRegistry;
import org.hero.tools.ToolRegistry;

import java.util.List;
import java.util.stream.Stream;

public class Program {

    private static LocalAi ai;
    private static Client gui;

    public Program() {
        gui = new ScannerInput();
        ai = new Llama3b();
    }

    public static Stream<Requests.Message> chat(String request) {
        if (CommandsRegistry.commandExists(request)) return CommandsRegistry.findAndRunCommand(request, ai, gui);

        Requests.RequestOut req = ai.chat(List.of(new Requests.Message("user", request)));
        List<Requests.Message> toolReplies = ToolRegistry.findAndRunTools(req.message().tool_calls());
        if (!toolReplies.isEmpty()) {
            req = ai.chat(toolReplies);
            for (Requests.Message toolCalls : toolReplies) {
                gui.toolsDisplay(toolCalls.content());
            }
        }
        return Stream.of(req.message());
    }

    public void launch() {
        gui.launchApplication();
    }
}

package org.hero;

import org.hero.chatai.Llama3b;
import org.hero.chatai.LocalAi;
import org.hero.chatai.Requests;
import org.hero.chatgui.Client;
import org.hero.chatgui.ScannerInput;
import org.hero.commands.CommandsRegistry;
import org.hero.tools.ToolRegistry;

import java.util.Optional;
import java.util.stream.Stream;

public class Program {

    private static LocalAi ai;
    private static Client gui;

    public Program() {
        gui = new ScannerInput();
        ai = new Llama3b();
    }

    public static Stream<Requests.Message> chat(String request) {
        if (CommandsRegistry.commandExists(request)) {
            return CommandsRegistry.findAndRunCommand(request, ai, gui);
        }
        Requests.RequestOut req = ai.chat(new Requests.Message("user", request));
        Optional<Requests.Message> message = ToolRegistry.detectAndRunTool(req.message().content());
        if (message.isPresent()) {
            req = ai.chat(message.get());
        }
        return Stream.of(req.message());
    }

    public void launch() {
        gui.launchApplication();
    }
}

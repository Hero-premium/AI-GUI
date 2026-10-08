package org.hero.commands;

import org.hero.Requests;
import org.hero.Roles;
import org.hero.chatai.LocalAi;
import org.hero.chatgui.Client;

import java.util.ArrayList;
import java.util.List;

public class GetAllCommands extends Command {


    public GetAllCommands() {
        super("/getAllCommands");
    }

    @Override
    protected List<Requests.Message> runCommand(LocalAi ai, Client client) {
        List<Requests.Message> messages = new ArrayList<>();
        for (Command command : CommandsRegistry.COMMANDS.values()) {
            messages.add(new Requests.Message(Roles.ASSISTANT, command.toString()));
        }
        return List.copyOf(messages);
    }
}
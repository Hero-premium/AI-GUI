package io.github.hero_premium.commands;

import io.github.hero_premium.Requests;
import io.github.hero_premium.Roles;
import io.github.hero_premium.chatgui.Client;
import io.github.hero_premium.program.conversations.Conversation;

import java.util.ArrayList;
import java.util.List;

public class GetAllCommands extends Command {


    public GetAllCommands() {
        super("/getAllCommands");
    }

    @Override
    protected List<Requests.Message> runCommand(Conversation conversation, Client client, String arguments) {
        List<Requests.Message> messages = new ArrayList<>();
        for (Command command : CommandsRegistry.COMMANDS.values()) {
            messages.add(new Requests.Message(Roles.ASSISTANT, command.toString()));
        }
        return List.copyOf(messages);
    }
}
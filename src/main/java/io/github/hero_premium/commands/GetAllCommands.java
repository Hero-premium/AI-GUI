package io.github.hero_premium.commands;

import io.github.hero_premium.Requests;
import io.github.hero_premium.clients.Client;
import io.github.hero_premium.program.conversations.Conversation;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class GetAllCommands extends Command {


    public GetAllCommands() {
        super("/getAllCommands");
    }

    @Override
    protected List<Requests.Message> runCommand(Conversation conversation, @NotNull Client client, String[] arguments) {
        String s = CommandsRegistry.COMMANDS.values().stream()
                .map(command -> command.toString() + "\n")
                .collect(Collectors.joining());
        client.endLine(s);
        return List.of();
    }
}
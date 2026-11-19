package io.github.hero_premium.commands;

import io.github.hero_premium.Requests;
import io.github.hero_premium.clients.Client;
import io.github.hero_premium.program.conversations.Conversation;
import io.github.hero_premium.tools.ToolRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class GetAllTools extends Command {

    public GetAllTools() {
        super("/getAllTools");
    }

    @Override
    protected @Nullable List<Requests.Message> runCommand(Conversation conversation, @NotNull Client client, String[] arguments) {
        String s = ToolRegistry.TOOLS.values().stream()
                .map(tool -> tool.toString() + "\n")
                .collect(Collectors.joining());
        client.endLine(s);
        return List.of();
    }
}

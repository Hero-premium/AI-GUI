package io.github.hero_premium.commands;

import io.github.hero_premium.Requests;
import io.github.hero_premium.Roles;
import io.github.hero_premium.chatgui.Client;
import io.github.hero_premium.program.conversations.Conversation;

import java.util.List;

public class GetConversationHistory extends Command {

    /**
     *
     * @throws NullPointerException     if commandLine was null
     * @throws IllegalArgumentException if the command given does not start with /
     */
    public GetConversationHistory() {
        super("/getConversationHistory");
    }

    @Override
    protected List<Requests.Message> runCommand(Conversation conversation, Client client, String arguments) {
        for (Requests.Message message : conversation.getMessages()) {
            String content = message.content().isEmpty() ? message.tool_calls().toString() : message.content();
            client.displayAIMessage(content);
        }
        return List.of(new Requests.Message(Roles.SYSTEM, ""));
    }
}

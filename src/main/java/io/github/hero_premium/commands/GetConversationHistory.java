package io.github.hero_premium.commands;

import io.github.hero_premium.Requests;
import io.github.hero_premium.chatai.LocalAi;
import io.github.hero_premium.chatgui.Client;

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
    protected List<Requests.Message> runCommand(LocalAi ai, Client client) {
        return ai.getMessages();
    }
}

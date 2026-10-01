package org.hero.commands;

import org.hero.chatai.LocalAi;
import org.hero.chatai.Requests;
import org.hero.chatgui.Client;

import java.util.stream.Stream;

public class getAllSentMessages extends Command {


    /**
     *
     * @throws NullPointerException     if commandLine was null
     * @throws IllegalArgumentException if the command given does not start with /
     */
    public getAllSentMessages() {
        super("/getAllSentMessages");
    }

    @Override
    protected Stream<Requests.Message> runCommand(LocalAi ai, Client client) {
        return ai.getMessages();
    }
}

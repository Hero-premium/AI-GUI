package org.hero.commands;

import org.hero.Requests;
import org.hero.chatai.LocalAi;
import org.hero.chatgui.Client;

import java.util.List;
import java.util.Objects;

/**
 * defines what a command is, the AI cannot see commands and doesn't know about them
 */
public abstract class Command {


    /**
     * the line the user will have to enter for the command to run, must start with a /
     */
    protected final String commandLine;

    /**
     *
     * @param commandLine the line the user will have to enter for the command to run
     * @throws NullPointerException     if commandLine was null
     * @throws IllegalArgumentException if the command given does not start with /
     */
    protected Command(String commandLine) {
        this.commandLine = Objects.requireNonNull(commandLine, "commandLine must not be null");
        if (!commandLine.startsWith("/")) throw new IllegalArgumentException("commandLine must start with /");
    }


    protected abstract List<Requests.Message> runCommand(LocalAi ai, Client client);

    @Override
    public String toString() {
        return "command " + getClass().getSimpleName();
    }
}

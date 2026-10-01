package org.hero.commands;

import org.hero.chatai.LocalAi;
import org.hero.chatai.Requests;
import org.hero.chatgui.Client;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.stream.Stream;

/**
 * defines what a command is, the AI cannot see commands and doesn't know about them
 */
public abstract class Command {

    private static final Map<String, Command> commands = new HashMap<>();

    static {
        for (Command command : ServiceLoader.load(Command.class)) {
            commands.put(command.commandLine, command);
        }
    }


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

    private static Command findCommand(String givenCommand) {
        Objects.requireNonNull(givenCommand, "givenCommand must not be null");
        return commands.get(givenCommand);
    }

    /**
     * returns true if the given string is a valid command
     *
     * @param givenCommand the command to be tested
     * @return true if the line is a command, otherwise false
     * @throws NullPointerException if givenCommand was null
     */
    public static boolean commandExists(String givenCommand) {
        return findCommand(givenCommand) != null;
    }


    /**
     * runs the given command and returns the result as a stream of messages, returns an empty stream is the command was invalid
     *
     * @param givenCommand the command to execute
     * @param ai           the current running AI
     * @param client       the current client
     * @return the command return, a stream of messages
     */
    public static Stream<Requests.Message> findAndRunCommand(String givenCommand, LocalAi ai, Client client) {
        Command commandLine = findCommand(givenCommand);
        if (commandLine == null) return Stream.empty();
        return commandLine.runCommand(ai, client);
    }

    protected abstract Stream<Requests.Message> runCommand(LocalAi ai, Client client);

}

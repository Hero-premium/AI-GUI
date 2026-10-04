package org.hero.commands;

import org.hero.Requests;
import org.hero.chatai.LocalAi;
import org.hero.chatgui.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Stream;

public final class CommandsRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandsRegistry.class);


    public static final Map<String, Command> COMMANDS;

    static {
        Map<String, Command> map = new HashMap<>();
        Stream<ServiceLoader.Provider<Command>> providerStream = ServiceLoader.load(Command.class).stream();

        providerStream.forEach(provider -> {
            try {
                Command command = provider.get();
                map.put(command.commandLine, command);
                LOGGER.info("{} has been added", command);
            } catch (ServiceConfigurationError e) {
                LOGGER.error("{} could not be loaded", e.getMessage());
            }
        });
        COMMANDS = Map.copyOf(map);
    }

    public static Command findCommand(String givenCommand) {
        return COMMANDS.get(Objects.requireNonNull(givenCommand, "givenCommand must not be null"));
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
        LOGGER.debug("looking for command {}", givenCommand);
        Command commandLine = findCommand(givenCommand);
        if (commandLine == null) {
            LOGGER.debug("could not find command {}", givenCommand);
            return Stream.empty();
        }
        LOGGER.debug("found {}", commandLine);
        return commandLine.runCommand(ai, client);
    }

    private CommandsRegistry() {
        throw new AssertionError("no org.hero.commands.CommandsRegistry instance for you!");
    }
}
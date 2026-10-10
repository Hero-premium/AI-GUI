package org.hero.commands;

import org.hero.Requests;
import org.hero.chatai.LocalAi;
import org.hero.chatgui.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Stream;

/**
 * all commands are registered here, also combinations methods for finding and running them safely.
 */
public final class CommandsRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandsRegistry.class);

    /**
     * A list containing every registered command
     */
    public static final Map<String, Command> COMMANDS;

    static {
        Map<String, Command> map = new HashMap<>();
        try {
            Stream<ServiceLoader.Provider<Command>> tools = ServiceLoader.load(Command.class).stream();
            tools.forEach(provider -> {
                try {
                    Command command = provider.get();
                    map.put(command.commandLine, command);
                    LOGGER.info("{} has been added", command);
                } catch (ServiceConfigurationError e) {
                    LOGGER.error("Could not be loaded: {}", e.getMessage(), e);
                }
            });
        } catch (ServiceConfigurationError e) {
            LOGGER.error("no commands were loaded: {}", e.getMessage(), e);
        }
        COMMANDS = Collections.unmodifiableMap(map);
    }

    /**
     * don't instantiate this
     */
    private CommandsRegistry() {
        throw new AssertionError("no org.hero.commands.CommandsRegistry instance for you!");
    }

    /**
     *
     * @param givenCommand the command line of the command
     * @return the command, null if the command doesn't exist
     * @throws NullPointerException if givenCommand was null
     *
     * @see #findAndRunCommand(String, LocalAi, Client)
     */
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
     * runs the given command and returns the result as a list of messages, returns an empty list if the command was invalid or if the command returned null
     *
     * @param givenCommand the command to execute
     * @param ai           the current running AI
     * @param client       the current client
     * @return the command return, a list of messages
     */
    public static List<Requests.Message> findAndRunCommand(String givenCommand, LocalAi ai, Client client) {
        Command commandLine = findCommand(givenCommand);
        LOGGER.debug("looking for command {}", givenCommand);
        if (commandLine == null) {
            LOGGER.debug("could not find command {}", givenCommand);
            return List.of();
        }
        LOGGER.debug("found {}", commandLine);
        List<Requests.Message> messages = commandLine.runCommand(ai, client);
        return messages == null ? List.of() : messages;
    }
}
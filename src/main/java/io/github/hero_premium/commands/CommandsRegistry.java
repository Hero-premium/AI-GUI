package io.github.hero_premium.commands;

import io.github.hero_premium.Requests;
import io.github.hero_premium.Role;
import io.github.hero_premium.clients.Client;
import io.github.hero_premium.program.conversations.Conversation;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * all commands are registered here, also methods for finding and running them safely.
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
            ServiceLoader.load(Command.class).stream().forEach(provider -> {
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
        throw new AssertionError("no io.github.hero_premium.commands.CommandsRegistry instance for you!");
    }

    /**
     *
     * @param givenCommand the command line of the command
     * @return the command, null if the command doesn't exist
     * @throws NullPointerException if givenCommand was null
     * @see #findAndRunCommand(String, Conversation, Client)
     */
    public static Command findCommand(@NotNull String givenCommand) {
        Objects.requireNonNull(givenCommand, "givenCommand must not be null");
        return COMMANDS.get(givenCommand.strip().toLowerCase(Locale.ROOT));
    }

    /**
     * runs the given command and returns the result as a list of messages,
     * returns a list containing one error message if the command was doesn't exist
     * returns an empty list if the command returned null
     *
     * @param givenCommand the command to execute
     * @param conversation           the current running conversation
     * @param client       the current client
     * @return the command return, a list of messages
     */
    public static @NotNull List<Requests.Message> findAndRunCommand(String givenCommand, Conversation conversation, Client client) {
        Command command = findCommand(givenCommand);
        LOGGER.debug("looking for command {}", givenCommand);
        if (command == null) {
            LOGGER.debug("could not find command {}", givenCommand);
            return List.of(new Requests.Message(Role.USER_FACING, "invalid command, use /getAllCommands for all commands"));
        }
        LOGGER.debug("found {}", command);
        List<Requests.Message> messages = command.runCommand(conversation, client, parseCommand(command, givenCommand));
        return messages == null ? List.of() : messages;
    }

    @Contract(value = "_, _ -> new", pure = true)
    static String @NotNull [] parseCommand(@NotNull Command command, @NotNull String givenCommand) {
        // TODO make "string here" counts as one arg.
        String s = givenCommand.strip().substring(command.commandLine.length()).strip();
        return s.isBlank() ? new String[0] : s.split("\\s+");
    }
}
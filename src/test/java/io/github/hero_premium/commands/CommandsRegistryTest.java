package io.github.hero_premium.commands;

import io.github.hero_premium.Requests;
import io.github.hero_premium.clients.Client;
import io.github.hero_premium.program.conversations.Conversation;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CommandsRegistryTest {

    @Test
    void findCommand() {
        Command command = CommandsRegistry.findCommand("aCommandThatDoesntExist");
        assertNull(command);
        command = CommandsRegistry.findCommand("/getAllCommands");
        assertNotNull(command);
        assertThrows(NullPointerException.class, () -> CommandsRegistry.findCommand(null));
    }

    @Test
    void findAndRunCommand() {
        Client client = new Client() {
            @Override
            public void launchApplication() {
            }

            @Override
            public void displayUserMessage(@NonNull String message) {
            }

            @Override
            public void displayToolsMessage(@NonNull String message) {
            }

            @Override
            public void displayAIMessage(@NonNull String message) {
            }

            @Override
            public void displaySystemMessage(@NonNull String message) {
            }

            @Override
            public void endLine() {

            }

            @Override
            public void endLine(String message) {

            }

            @Override
            public void displayThinking(String message) {

            }

            @Override
            public void endLineOfThinking() {

            }
        };

        List<Requests.Message> message = CommandsRegistry.findAndRunCommand(
                "aCommandThatDoesntExist", null, client);
        assertEquals("invalid command, use /getAllCommands for all commands", message.getFirst().content());
    }

    @Test
    void noArgs() {
        assertEquals(0, CommandsRegistry.parseCommand(createCommand("/showAMovie"), "/showAMovie").length);
    }

    private @NonNull Command createCommand(String command) {
        return new Command(command) {
            @Override
            protected @NonNull List<Requests.Message> runCommand(Conversation conversation, Client client, String[] arguments) {
                return List.of();
            }
        };
    }

    @Test
    void oneArg() {
        List<String> list = Arrays.asList(CommandsRegistry.parseCommand(createCommand("/myCatLovesToEat"),
                "/myCatLovesToEat it really loves to"));

        assertEquals(List.of("it", "really", "loves", "to"), list);
    }

    @Test
    void parseWithSpaces() {
        List<String> list = Arrays.asList(CommandsRegistry.parseCommand(createCommand("/myCatLovesToEat"),
                "            /myCatLovesToEat     it      really    loves    to       "));

        assertEquals(List.of("it", "really", "loves", "to"), list);
    }
}
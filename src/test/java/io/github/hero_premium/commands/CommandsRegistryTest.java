package io.github.hero_premium.commands;

import io.github.hero_premium.Requests;
import io.github.hero_premium.clients.Client;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

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
    void commandExists() {
        assertFalse(CommandsRegistry.isACommand("aCommandThatDoesntExist"));
        assertTrue(CommandsRegistry.isACommand("/getAllCommands"));
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
}
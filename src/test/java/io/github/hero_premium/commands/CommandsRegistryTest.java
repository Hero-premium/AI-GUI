package io.github.hero_premium.commands;

import io.github.hero_premium.Requests;
import io.github.hero_premium.chatgui.Client;
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
            public void displayUserMessage(String message) {
            }

            @Override
            public void displayToolsMessage(String message) {
            }

            @Override
            public void displayAIMessage(String message) {
            }

            @Override
            public void displaySystemMessage(String message) {
            }
        };

        List<Requests.Message> message = CommandsRegistry.findAndRunCommand(
                "aCommandThatDoesntExist", null, client);
        assertEquals(0, message.size());
    }
}
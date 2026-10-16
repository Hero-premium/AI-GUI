package org.hero.commands;

import org.hero.Requests;
import org.hero.chatai.LocalAi;
import org.hero.chatgui.Client;
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
        assertFalse(CommandsRegistry.commandExists("aCommandThatDoesntExist"));
        assertTrue(CommandsRegistry.commandExists("/getAllCommands"));
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
        LocalAi localAi = new LocalAi("") {
            @Override
            public Requests.RequestOut chat(List<Requests.Message> messages) {
                return null;
            }
        };

        List<Requests.Message> message = CommandsRegistry.findAndRunCommand(
                "aCommandThatDoesntExist", localAi, client);
        assertEquals(0, message.size());
    }
}
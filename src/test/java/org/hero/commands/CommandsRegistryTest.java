package org.hero.commands;

import org.hero.Requests;
import org.hero.chatai.Llama3b;
import org.hero.chatgui.ScannerInput;
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
        List<Requests.Message> message = CommandsRegistry.findAndRunCommand(
                "aCommandThatDoesntExist", new Llama3b(), new ScannerInput());
        assertEquals(0, message.size());
    }
}
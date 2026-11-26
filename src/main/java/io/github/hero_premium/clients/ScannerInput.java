package io.github.hero_premium.clients;


import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

@SuppressWarnings("unused")
public class ScannerInput extends Client {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScannerInput.class);

    @Override
    public void launchApplication() {
        try (Scanner scanner = new Scanner(System.in)) {
            while (scanner.hasNextLine()) handleChat(scanner.nextLine());
        }
        displaySystemMessage("thanks for using this application.");
    }

    private void handleChat(String nextLine) {
        try {
            program.chat(nextLine);
        } catch (Exception e) {
            LOGGER.error("something went wrong", e);
        }
    }

    @Override
    public void displayUserMessage(@NotNull String message) {
        IO.println("user: " + message);
    }

    @Override
    public void displayToolsMessage(@NotNull String message) {
        IO.println("TOOLS [ " + message + " ]");
    }

    @Override
    public void displayAIMessage(@NotNull String message) {
        IO.print(message);
    }

    @Override
    public void displaySystemMessage(@NotNull String message) {
        IO.println("system: " + message);
    }

    @Override
    public void endLine() {
        endLine("User turn!");
    }

    @Override
    public void endLine(String message) {
        IO.println(message);
    }

    @Override
    public void displayThinking(String message) {
        IO.print(message);
    }

    @Override
    public void endLineOfThinking() {
        IO.println("reply:");
    }
}

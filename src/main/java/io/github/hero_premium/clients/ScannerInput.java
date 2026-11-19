package io.github.hero_premium.clients;


import org.jetbrains.annotations.NotNull;

import java.util.Scanner;

public class ScannerInput extends Client {
    private final Scanner scanner;

    public ScannerInput() {
        scanner = new Scanner(System.in);
    }

    @Override
    public void launchApplication() {
        while (scanner.hasNextLine()) {
            String input = scanner.nextLine();
            program.chat(input);
            displaySystemMessage("User turn!");
        }
        displaySystemMessage("you have just shut this down");
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
        IO.println();
    }
}

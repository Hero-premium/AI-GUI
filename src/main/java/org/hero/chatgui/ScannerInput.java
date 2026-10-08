package org.hero.chatgui;

import org.hero.Program;

import java.util.Scanner;

public class ScannerInput implements Client {
    private final Scanner scanner;

    public ScannerInput() {
        scanner = new Scanner(System.in);
    }

    @Override
    public void launchApplication() {
        while (true) {
            if (scanner.hasNextLine()) {
                String input = scanner.nextLine();
                Program.chat(input);
                displaySystemMessage("User turn!");
                } else {
                displaySystemMessage("you have just shut this down");
                break;
            }
        }
    }

    @Override
    public void displayUserMessage(String message) {
        IO.println("user: " + message);
    }

    @Override
    public void displayToolsMessage(String message) {
        IO.println("TOOLS [ " + message + " ]");
    }

    @Override
    public void displayAIMessage(String message) {
        IO.println("AI: " + message);
    }

    @Override
    public void displaySystemMessage(String message) {
        IO.println("system: " + message);
    }
}

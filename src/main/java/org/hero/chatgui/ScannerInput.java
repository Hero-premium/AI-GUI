package org.hero.chatgui;

import org.hero.Program;

import java.util.Scanner;

public class ScannerInput implements Client {
    Scanner scanner;

    public ScannerInput() {
        scanner = new Scanner(System.in);
    }

    @Override
    public void launchApplication() {
        while (true) {
            if (scanner.hasNextLine()) {
                String input = scanner.nextLine();
                Program.chat(input).forEach(message -> displayMessage(message.content()));
                displayMessage("---- User turn!");
            }
        }
    }

    @Override
    public void displayMessage(String message) {
        IO.println(message);
    }
}

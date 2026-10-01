package org.hero.chatgui;

import org.hero.Program;
import org.hero.chatai.Requests;

import java.util.Scanner;

public class ScannerInput implements Client {
    Scanner scanner;

    public ScannerInput(){
        scanner = new Scanner(System.in);
    }

    @Override
    public void launchApplication() {
        while (true){
            if (scanner.hasNextLine()) {
                String input = scanner.nextLine();
                IO.println(Program.ai.chat(new Requests.Message("user", input)).message().content());
                IO.println("---- User turn!");
            }
        }
    }
}

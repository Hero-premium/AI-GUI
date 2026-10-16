package io.github.hero_premium.chatgui;

import io.github.hero_premium.program.Program;

// this class is under reconstruction
public class AiTalksToAi extends Client {

    Program program2 = new Program(this);
    private String ai1Input = "we need to make a new programming language with unique features, keep your responds short ";
    private String ai2Input = "";

    @Override
    public void launchApplication() {
        while (true) {
            program2.chat(ai1Input);
            program.chat(ai2Input);
        }
    }


    @Override
    public void displayUserMessage(String message) {
        IO.println("AI 2- " + message);
    }

    @Override
    public void displayToolsMessage(String message) {
        IO.println("TOOL [ " + message + " ]");
    }

    @Override
    public void displayAIMessage(String message) {
        IO.println("AI 1" + message);
    }

    @Override
    public void displaySystemMessage(String message) {
        IO.println("SYSTEM [ " + message + " ]");
    }
}
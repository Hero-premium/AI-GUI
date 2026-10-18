package io.github.hero_premium.chatgui;


import io.github.hero_premium.program.Program;

//FIXME double display bug
public class AiTalksToAi extends Client {

    public static final int MAX_TURNS = 100;
    Program program2 = new Program(this);
    private final String[] aiInputs = new String[2];

    private Turns turn = Turns.AI1;

    public AiTalksToAi() {
        aiInputs[0] = "hi";
        aiInputs[1] = "";
    }

    @Override
    public void launchApplication() {
        for (int i = 0; i < MAX_TURNS; i++) {
            program2.chat(aiInputs[0]);
            program.chat(aiInputs[1]);
        }
        IO.println("max turns reached");
    }


    @Override
    public void displayUserMessage(String message) {
        switch (turn) {
            case AI1 -> {
                aiInputs[0] = message;
                IO.println("AI 1 " + message);
                turn = Turns.AI2;
            }
            case AI2 -> {
                aiInputs[1] = message;
                IO.println("AI 2 " + message);
                turn = Turns.AI1;
            }
            default -> throw new AssertionError("Unknown turn");
        }
    }

    @Override
    public void displayToolsMessage(String message) {
        IO.println("TOOL [ " + message + " ]");
    }

    @Override
    public void displayAIMessage(String message) {
        switch (turn) {
            case AI1 -> {
                aiInputs[0] = message;
                IO.println("AI 1 " + message);
            }
            case AI2 -> {
                aiInputs[1] = message;
                IO.println("AI 2 " + message);
            }
            default -> throw new AssertionError("Unknown turn");
        }
    }


    @Override
    public void displaySystemMessage(String message) {
        IO.println("SYSTEM [ " + message + " ]");
    }

    private enum Turns {
        AI1, AI2
    }
}
package org.hero.chatgui;

import org.hero.Program;
import org.hero.chatai.Llama3b;
import org.hero.chatai.LocalAi;
import org.hero.Requests;

import java.util.List;

public class AiTalksToAi implements Client {


    private final StringBuilder builder = new StringBuilder();
    LocalAi ai = new Llama3b();
    private String ai1Input = "we need to make a new programming language with unique features, keep your responds short ";

    @Override
    public void launchApplication() {
        while (true) {
            displayMessage("AI 1- " + ai1Input);
            String ai2Input = ai.chat(List.of(new Requests.Message("user", ai1Input))).message().content();
            displayMessage("AI 2- " + ai2Input);
            Program.chat(ai2Input).forEach(message -> builder.append(message.content()));
            ai1Input = builder.toString();
            builder.setLength(0);
        }
    }

    @Override
    public void displayMessage(String message) {
        IO.println(message);
    }

    @Override
    public void toolsDisplay(String toolName) {

    }
}

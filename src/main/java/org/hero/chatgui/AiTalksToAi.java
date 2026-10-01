package org.hero.chatgui;

import org.hero.Program;
import org.hero.chatai.Llama3b;
import org.hero.chatai.LocalAi;
import org.hero.chatai.Requests;

public class AiTalksToAi implements Client {


    private final StringBuilder builder = new StringBuilder();
    LocalAi ai = new Llama3b();
    private String ai1Input = "we need to make a new programming language with unique features, keep your responds short ";

    @Override
    public void launchApplication() {
        while (true) {
            displayMessage("AI 1- " + ai1Input);
            String ai2Input = ai.chat(new Requests.Message("user", ai1Input)).message().content();
            displayMessage("AI 2- " + ai2Input);
            Program.chat(ai2Input).forEach(builder::append);
            ai1Input = builder.toString();
            builder.setLength(0);
        }
    }

    @Override
    public void displayMessage(String message) {
        IO.println(message);
    }
}

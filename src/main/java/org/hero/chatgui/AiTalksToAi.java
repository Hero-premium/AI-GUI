package org.hero.chatgui;

import org.hero.Requests;
import org.hero.Roles;
import org.hero.chatai.Llama3b;
import org.hero.chatai.LocalAi;

import java.util.List;

public class AiTalksToAi extends Client {


    private final StringBuilder builder = new StringBuilder();
    LocalAi ai = new Llama3b();
    private String ai1Input = "we need to make a new programming language with unique features, keep your responds short ";

    @Override
    public void launchApplication() {
        while (true) {
            displayAIMessage(ai1Input);
            String ai2Input = ai.chat(List.of(new Requests.Message(Roles.USER, ai1Input))).message().content();
            displayUserMessage(ai2Input);
            program.chat(ai2Input);
            ai1Input = builder.toString();
            builder.setLength(0);
        }
    }

    @Override
    public void displayUserMessage(String message) {
         builder.append(message);
        IO.println("AI 2- " + message);
    }

    @Override
    public void displayToolsMessage(String message) {
        IO.println("TOOL [ " + message + " ]");
    }

    @Override
    public void displayAIMessage(String message) {
        IO.println("AI 1" + message );
    }

    @Override
    public void displaySystemMessage(String message) {
        IO.println("SYSTEM [ " + message + " ]");
    }
}
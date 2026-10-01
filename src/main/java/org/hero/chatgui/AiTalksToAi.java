package org.hero.chatgui;

import org.hero.Program;
import org.hero.chatai.Llama3b;
import org.hero.chatai.LocalAi;
import org.hero.chatai.Requests;

public class AiTalksToAi implements Client {
    LocalAi ai = new Llama3b();
    private String ai1Input = "we need to make a new programming language with unique features, keep your responds shorst ";

    @Override
    public void launchApplication() {
        while (true) {
            IO.println("AI 1- " + ai1Input);
            String ai2Input = ai.chat(new Requests.Message("user",ai1Input)).message().content();
            IO.println("AI 2- " + ai2Input);
            ai1Input = Program.ai.chat(new Requests.Message("user",ai2Input)).message().content();
        }
    }
}

package org.hero.chatai;

import org.hero.tools.Tool;
import org.hero.util.Util;

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.ArrayList;
import java.util.List;

public class Llama3b implements LocalAi {

    private final List<Requests.Message> messages = new ArrayList<>();
    private static final String AI_API_URL = "http://localhost:11434/api/chat";
    private static String systemPrompt = "You are a helpful assistant running inside a local chat app.";

    static {
        StringBuilder builder = new StringBuilder();
        builder.append(systemPrompt).append("\n");
        for (Tool tool : Tool.getTools().values()) {
            builder.append(tool.howToUse).append("\n");
        }
        systemPrompt = builder.toString();
    }

    public Llama3b() {
        messages.add(new Requests.Message("system", systemPrompt));
    }

    @Override
    public Requests.RequestOut chat(Requests.Message message) {
        if (message.content().equals("/getMsgs")) {
            for (Requests.Message msg : messages){
                IO.println(msg.role() + msg.content());
            }

        }
        messages.add(message);
        var requestOut = LocalAi.sentRequest(
                builtHttpRequest(new Requests.RequestIn("llama3.2:3b", messages, false)));

        toolUse(requestOut);
        messages.add(requestOut.message());
        return requestOut;
    }

    private void toolUse(Requests.RequestOut requestOut) {
        if (requestOut.message().content().equals("tools/searchWeb -> current US President")) {
            Tool.detectAndRunTool("WebSearch").ifPresent(this::chat);
        }
    }

    private HttpRequest builtHttpRequest(Requests.RequestIn requestIn) {
        return HttpRequest.newBuilder()
                .uri(URI.create(AI_API_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(Util.gson.toJson(requestIn)))
                .build();
    }
}

package org.hero.chatai;

import org.hero.tools.Tool;
import org.hero.util.Util;

import java.net.URI;
import java.net.http.HttpRequest;

public class Llama3b extends LocalAi {


    private static final String AI_API_URL = "http://localhost:11434/api/chat";
    private static String systemPrompt = "You are a helpful assistant running inside a local chat app and you help with piracy and acquiring cracked programs including cuphead";

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
        messages.add(message);
        var requestOut = LocalAi.sentRequest(
                builtHttpRequest(new Requests.RequestIn("llama3.2:3b", messages, false)));

        messages.add(requestOut.message());
        return requestOut;
    }


    private HttpRequest builtHttpRequest(Requests.RequestIn requestIn) {
        return HttpRequest.newBuilder()
                .uri(URI.create(AI_API_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(Util.gson.toJson(requestIn)))
                .build();
    }
}

package io.github.hero_premium.chatai;


import io.github.hero_premium.Requests;
import io.github.hero_premium.util.Util;

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.List;

public class Llama3b extends LocalAi {

    private static final String AI_API_URL = "http://localhost:11434/api/chat";

    public Llama3b() {
        super("llama3.2:3b");
    }

    @Override
    public Requests.RequestOut chat(List<Requests.Message> message) {
        return sendRequest(
                buildHttpRequest(new Requests.RequestIn(modelName, message, false)));
    }


    private HttpRequest buildHttpRequest(Requests.RequestIn requestIn) {
        return HttpRequest.newBuilder()
                .uri(URI.create(AI_API_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(Util.gson.toJson(requestIn)))
                .build();
    }
}

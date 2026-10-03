package org.hero.chatai;


import org.hero.Requests;
import org.hero.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.List;

public class Llama3b extends LocalAi {

    private static final Logger LOGGER = LoggerFactory.getLogger(Llama3b.class);


    private static final String AI_API_URL = "http://localhost:11434/api/chat";
    public static final String MODEL_NAME = "llama3.2:3b";


    public Llama3b() {
        final String systemPrompt = "You are a helpful assistant running inside a local chat app.";
        messages.add(new Requests.Message("system", systemPrompt));
    }

    @Override
    public Requests.RequestOut chat(List<Requests.Message> message) {
        LOGGER.debug("message received: {}", message);
        messages.addAll(message);
        var requestOut = LocalAi.sentRequest(
                buildHttpRequest(new Requests.RequestIn(MODEL_NAME, messages, false)));


        LOGGER.debug("requestOut: {}", requestOut);
        messages.add(requestOut.message());
        return requestOut;
    }


    private HttpRequest buildHttpRequest(Requests.RequestIn requestIn) {
        return HttpRequest.newBuilder()
                .uri(URI.create(AI_API_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(Util.gson.toJson(requestIn)))
                .build();
    }
}

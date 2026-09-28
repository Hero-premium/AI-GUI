package org.hero.chatai;

import org.hero.util.Util;

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.List;

public class Llama3b implements LocalAi {


    @Override
    public Requests.RequestOut chat(String content) {
        var messages = List.of(new Requests.Message("user", content));
        var requestIn = new Requests.RequestIn("llama3.2:3b", messages, false);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:11434/api/chat"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(Util.gson.toJson(requestIn)))
                .build();

        return LocalAi.requestOut(request);
    }
}

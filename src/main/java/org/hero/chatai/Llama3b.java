package org.hero.chatai;

import org.hero.util.Util;

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.ArrayList;
import java.util.List;

public class Llama3b implements LocalAi {

    List<Requests.Message> messages = new ArrayList<>();

    @Override
    public Requests.RequestOut chat(String content) {
        messages.add(new Requests.Message("user", content));
        var requestOut = LocalAi.requestOut(
                builtHttpRequest(new Requests.RequestIn("llama3.2:3b", messages, false)));
        messages.add(requestOut.message());
        return requestOut;
    }

    private HttpRequest builtHttpRequest(Requests.RequestIn requestIn) {
        return HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:11434/api/chat"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(Util.gson.toJson(requestIn)))
                .build();
    }
}

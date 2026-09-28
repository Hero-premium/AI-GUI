package org.hero.chatai;

import org.hero.util.Util;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public interface LocalAi {


    HttpClient client = HttpClient.newHttpClient();

    static Requests.RequestOut requestOut(HttpRequest request) {
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return Util.gson.fromJson(response.body(), Requests.RequestOut.class);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    Requests.RequestOut chat(String content);
}

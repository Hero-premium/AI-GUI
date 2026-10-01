package org.hero.chatai;

import org.hero.util.Util;

import java.io.IOException;
import java.net.ConnectException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * the interface every AI configuration is meant to implement
 */
public interface LocalAi {


    HttpClient client = HttpClient.newHttpClient();

    static Requests.RequestOut sentRequest(HttpRequest request) {
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return Util.gson.fromJson(response.body(), Requests.RequestOut.class);
        } catch (ConnectException _) {
            return new Requests.RequestOut("system", "", new Requests.Message("system", "ollama server is down"), true, "", 0, 0, 0, 0, 0, 0, 0);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    Requests.RequestOut chat(Requests.Message message);
}

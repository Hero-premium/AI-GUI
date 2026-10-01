package org.hero.chatai;

import org.hero.util.Util;

import java.io.IOException;
import java.net.ConnectException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * the interface every AI configuration is meant to implement
 */
public abstract class LocalAi {

    protected final List<Requests.Message> messages = new ArrayList<>();
    private static final HttpClient client = HttpClient.newHttpClient();



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

    public Stream<Requests.Message> getMessages() {
        return messages.stream();
    }

    public abstract Requests.RequestOut chat(Requests.Message message);
}

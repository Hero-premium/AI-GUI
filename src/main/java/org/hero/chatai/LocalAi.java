package org.hero.chatai;

import org.hero.Requests;
import org.hero.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ConnectException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * the interface every AI configuration is meant to implement
 */
public abstract class LocalAi {


    private static final Logger LOGGER = LoggerFactory.getLogger(LocalAi.class);


    private static final HttpClient client = HttpClient.newHttpClient();
    protected final List<Requests.Message> messages = new ArrayList<>();

    static Requests.RequestOut sentRequest(HttpRequest request) {
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            LOGGER.debug("requestOut: {}", response);
            return Util.gson.fromJson(response.body(), Requests.RequestOut.class);
        } catch (ConnectException _) {
            return new Requests.RequestOut("system", "", new Requests.Message("system", "ollama server is down"), true, "", 0, 0, 0, 0, 0, 0, 0);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Requests.Message> getMessages() {
        return List.copyOf(messages);
    }

    public abstract Requests.RequestOut chat(List<Requests.Message> messages);
}

package org.hero.chatai;

import org.hero.Requests;
import org.hero.Roles;
import org.hero.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ConnectException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * the interface every AI configuration is meant to implement
 */
public abstract class LocalAi {


    private static final Logger LOGGER = LoggerFactory.getLogger(LocalAi.class);
    private boolean isThinking = false;



    protected final List<Requests.Message> messages = new ArrayList<>();

    Requests.RequestOut sendRequest(HttpRequest request) {
        isThinking = true;
        try {
            HttpResponse<String> response = Util.client.send(request, HttpResponse.BodyHandlers.ofString());
            LOGGER.debug("requestOut: {}", response);
            return Util.gson.fromJson(response.body(), Requests.RequestOut.class);
        } catch (ConnectException _) {
            return new Requests.RequestOut("system", "", new Requests.Message(Roles.SYSTEM, "ollama server is down"), true, "", 0, 0, 0, 0, 0, 0, 0);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            isThinking = false;
        }
    }

    public List<Requests.Message> getMessages() {
        return List.copyOf(messages);
    }

    public abstract Requests.RequestOut chat(List<Requests.Message> messages);

    public boolean isThinking() {
        return isThinking;
    }
}
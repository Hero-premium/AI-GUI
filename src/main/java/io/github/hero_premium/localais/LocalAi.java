package io.github.hero_premium.localais;

import io.github.hero_premium.Requests;
import io.github.hero_premium.Roles;
import io.github.hero_premium.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ConnectException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.util.List;
import java.util.Objects;

/**
 * the interface every AI configuration is meant to implement
 */
public abstract class LocalAi {


    private static final Logger LOGGER = LoggerFactory.getLogger(LocalAi.class);
    public final String modelName;
    private boolean isThinking = false;

    protected LocalAi(String modelName) {
        this.modelName = Objects.requireNonNull(modelName, "modelName must not be null");
    }

    Requests.RequestOut sendRequest(HttpRequest request) {
        isThinking = true;
        try {
            HttpResponse<String> response = Util.client.send(request, HttpResponse.BodyHandlers.ofString());
            LOGGER.debug("requestOut: {}", response.body());
            return Util.gson.fromJson(response.body(), Requests.RequestOut.class);
        } catch (ConnectException e) {
            LOGGER.error("connection failed: ", e);
            return new Requests.RequestOut(modelName, LocalDateTime.now().toString(), new Requests.Message(Roles.USER_FACING,
                    "couldn't reach ollama server, please check that ollama server is up (try 'ollama serve' in the cmd)"),
                    true, "failer", 0, 0, 0, 0, 0, 0, 0);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            isThinking = false;
        }
    }

    public abstract Requests.RequestOut chat(List<Requests.Message> messages);

    public boolean isThinking() {
        return isThinking;
    }
}
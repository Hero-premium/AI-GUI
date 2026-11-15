package io.github.hero_premium.localais;

import io.github.hero_premium.Requests;
import io.github.hero_premium.Requests.RequestOut;
import io.github.hero_premium.util.Util;
import org.jetbrains.annotations.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * the class every AI configuration is meant to extend, provides methods for networking.
 */
public abstract class LocalAi {


    private static final Logger LOGGER = LoggerFactory.getLogger(LocalAi.class);
    public final String modelName;
    private volatile boolean isThinking = false;

    public LocalAi(String modelName) {
        this.modelName = Objects.requireNonNull(modelName, "modelName must not be null");
    }

    protected void sendRequest(HttpRequest request, Consumer<RequestOut> responseConsumer) {
        if (isThinking) throw new IllegalStateException("cannot accept more than one request at a time");
        isThinking = true;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        Util.client.send(request, HttpResponse.BodyHandlers.ofInputStream()).body(),
                        StandardCharsets.UTF_8))) {
            RequestOut requestOut;
            do {
                String json = reader.readLine();
                requestOut = Util.gson.fromJson(json, RequestOut.class);
                isThinking = !requestOut.done();
                responseConsumer.accept(requestOut);
            } while (!requestOut.done());
        } catch (ConnectException e) {
            LOGGER.error("connection failed: ", e);
            responseConsumer.accept(RequestOut.defaultsIn(modelName,
                            """
                            couldn't reach ollama server,
                            please check that ollama server is up
                            (try 'ollama serve' in the cmd)"""
            ));
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            isThinking = false;
        }
    }

    public abstract void chat(List<Requests.Message> messages, Consumer<RequestOut> out);

    @Contract(pure = true)
    public boolean isThinking() {
        return isThinking;
    }
}
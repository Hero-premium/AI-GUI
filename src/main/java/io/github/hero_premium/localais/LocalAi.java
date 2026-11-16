package io.github.hero_premium.localais;

import io.github.hero_premium.Requests;
import io.github.hero_premium.Requests.RequestOut;
import io.github.hero_premium.util.Util;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
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
        try (BufferedReader reader = createBufferedReader(request)) {
            readLines(responseConsumer, reader);
        } catch (ConnectException e) {
            LOGGER.error("connection failed: ", e);
            responseConsumer.accept(getOllamaDownReqOut());
        } catch (IOException e) {
            LOGGER.error("IO error: ", e);
            responseConsumer.accept(RequestOut.defaultsIn(modelName, "something went wrong while generating this response"));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.error("Interrupted: ", e);
            responseConsumer.accept(RequestOut.defaultsIn(modelName, "something went wrong while generating this response"));
        } finally {
            isThinking = false;
        }
    }

    private void readLines(@NotNull Consumer<RequestOut> responseConsumer, @NotNull BufferedReader reader) throws IOException {
        RequestOut requestOut;
        do {
            requestOut = Util.gson.fromJson(reader.readLine(), RequestOut.class);
            isThinking = !requestOut.done();
            responseConsumer.accept(requestOut);
        } while (!requestOut.done());
    }

    private @NotNull RequestOut getOllamaDownReqOut() {
        return RequestOut.defaultsIn(modelName,
                """
                        couldn't reach ollama server,
                        please check that ollama server is up
                        (try 'ollama serve' in the cmd)"""
        );
    }

    private @NotNull
    static BufferedReader createBufferedReader(HttpRequest request) throws IOException, InterruptedException {
        return new BufferedReader(
                new InputStreamReader(
                        Util.client.send(request, HttpResponse.BodyHandlers.ofInputStream()).body(),
                        StandardCharsets.UTF_8));
    }

    public abstract void chat(List<Requests.Message> messages, Consumer<RequestOut> out);

    @Contract(pure = true)
    public boolean isThinking() {
        return isThinking;
    }
}
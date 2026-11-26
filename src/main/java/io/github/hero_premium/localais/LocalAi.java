package io.github.hero_premium.localais;

import io.github.hero_premium.Requests;
import io.github.hero_premium.Requests.RequestOut;
import io.github.hero_premium.Role;
import io.github.hero_premium.util.Util;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * the class every AI configuration is meant to extend, provides methods for networking.
 */
public class LocalAi {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalAi.class);
    private static final String AI_API_URL = "http://localhost:11434/api/chat";

    public final String modelName;

    private final AtomicBoolean isBusy = new AtomicBoolean(false);
    private volatile boolean interrupted = false;

    protected LocalAi(String modelName) {
        this.modelName = Objects.requireNonNull(modelName, "modelName must not be null");
    }

    protected void sendRequest(HttpRequest request, Consumer<RequestOut> responseConsumer) {
        // if the model is already thinking isBusy would be true and the check will fail therefor throw
        if (!isBusy.compareAndSet(false, true))
            throw new IllegalStateException("cannot accept more than one request at a time");
        try (BufferedReader reader = createBufferedReader(request)) {
            readLines(responseConsumer, reader);
        } catch (ConnectException e) {
            LOGGER.error("connection failed: ", e);
            responseConsumer.accept(ollamaDownMessage());
        } catch (IOException e) {
            LOGGER.error("IO error: ", e);
            responseConsumer.accept(genericFailureMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.error("Interrupted: ", e);
            responseConsumer.accept(genericFailureMessage());
        } finally {
            isBusy.set(false);
            interrupted = false;
        }
    }

    private @NotNull RequestOut genericFailureMessage() {
        return RequestOut.failureDefaultsIn(modelName, "something went wrong while generating this response");
    }

    private void readLines(@NotNull Consumer<RequestOut> responseConsumer, @NotNull BufferedReader reader) throws IOException {
        RequestOut requestOut;
        do {
            if (interrupted) {
                responseConsumer.accept(RequestOut.failureDefaultsIn(modelName, "this response was interrupted"));
                break;
            }
            requestOut = Util.gson.fromJson(reader.readLine(), RequestOut.class);
            isBusy.set(!requestOut.done());
            responseConsumer.accept(requestOut);
        } while (!requestOut.done());
    }

    private @NotNull RequestOut ollamaDownMessage() {
        return RequestOut.failureDefaultsIn(modelName,
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

    public void chat(List<Requests.Message> messages, Consumer<Requests.RequestOut> out) {
        var userFacingList = getUserFacingList(messages);
        if (!userFacingList.isEmpty())
            throw new IllegalArgumentException("a user facing message must not reach the ai " + userFacingList);
        sendRequest(LocalAi.buildHttpRequest(new Requests.RequestIn(modelName, messages)), out);
    }

    private static @NotNull @Unmodifiable List<Requests.Message> getUserFacingList(@NonNull List<Requests.Message> messages) {
        return messages.stream()
                .filter(message -> message.role() == Role.USER_FACING)
                .toList();
    }

    @Contract(pure = true)
    public boolean isBusy() {
        return isBusy.get();
    }

    public void interrupt() {
        if (!isBusy.get()) throw new IllegalStateException("cannot be interrupted if the ai is not thinking");
        this.interrupted = true;
    }

    static HttpRequest buildHttpRequest(Requests.RequestIn requestIn) {
        return HttpRequest.newBuilder()
                .uri(URI.create(AI_API_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(Util.gson.toJson(requestIn)))
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || o.getClass() != this.getClass()) return false;
        LocalAi that = (LocalAi) o;
        return Objects.equals(this.modelName, that.modelName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(modelName);
    }

    @Override
    public String toString() {
        return "AI: " + modelName;
    }
}
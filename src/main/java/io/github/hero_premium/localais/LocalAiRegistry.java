package io.github.hero_premium.localais;

import com.google.gson.JsonSyntaxException;
import io.github.hero_premium.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class LocalAiRegistry {

    public static final String AI_MODELS_LINK = "http://localhost:11434/api/tags";
    private static final Logger LOGGER = LoggerFactory.getLogger(LocalAiRegistry.class);
    /**
     * an unmodifiable map containing every AI
     */
    public static final Map<String, LocalAiStorer> AIS;

    static {
        Map<String, LocalAiStorer> map = new HashMap<>();
        fetchOllamaModels(map);
        mergeConfigs(map);
        AIS = Collections.unmodifiableMap(map);
    }

    private static void mergeConfigs(Map<String, LocalAiStorer> map) {
        try {
           ServiceLoader.load(LocalAi.class).stream().forEach(provider -> {
                try {
                    /*
                     * if ollama already provided the config class this means the class is available,
                     * but if ollama did not (just the modder) it means that it's probably just a stale file,
                     * and shouldn't be used since ollama will reject it later
                     *
                     * we don't just make it disappear because that would be confusing to the user
                     * who forgot they deleted that model 3 months ago
                     *
                     * the overwrite is intentional in case someone wants one AI model config to be different.
                     */
                    LocalAi ai = provider.get();
                    LOGGER.info("loaded {} from config", ai.modelName);
                    boolean available = map.containsKey(ai.modelName);
                    if (!available) LOGGER.warn("model config {} loaded with no real model in ollama", ai.modelName);
                    map.put(ai.modelName, new LocalAiStorer(ai, available));
                } catch (ServiceConfigurationError e) {
                    LOGGER.error("Could not be loaded", e);
                }
            });

        } catch (ServiceConfigurationError e) {
            LOGGER.error("no Ais were loaded", e);
        }
    }

    private static void fetchOllamaModels(Map<String, LocalAiStorer> map) {
        try {
            HttpResponse<String> json = Util.client.send(httpRequestBuilder(), HttpResponse.BodyHandlers.ofString());
            Util.gson.fromJson(json.body(), ModelList.class)
                    .models()
                    .stream()
                    .map(ModelEntry::name)
                    .forEach(name -> {
                        map.put(name, new LocalAiStorer(new LocalAi(name), true));
                        LOGGER.info("{} has been added", name);
                    });
        } catch (JsonSyntaxException e) {
            LOGGER.error("failed to parse json sent by ollama", e);
        } catch (IOException e) {
            LOGGER.error("Error while trying to send local AI registration.", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.error("Error while trying to send local AI registration.", e);
        }
    }

    /**
     * don't instantiate this
     */
    private LocalAiRegistry() {
        throw new AssertionError("no io.github.hero_premium.LocalAiRegistry.Local instance for you!");
    }

    private static HttpRequest httpRequestBuilder() {
        return HttpRequest.newBuilder()
                .uri(URI.create(AI_MODELS_LINK))
                .GET()
                .build();
    }

    public record ModelEntry(String name) {
    }

    public record ModelList(List<ModelEntry> models) {
    }

}
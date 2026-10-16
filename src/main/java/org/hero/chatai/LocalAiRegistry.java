package org.hero.chatai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Stream;

public class LocalAiRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalAiRegistry.class);
    /**
     * an unmodifiable map containing every AI
     */
    public static final Map<String, LocalAi> AIS;

    static {
        Map<String, LocalAi> map = new HashMap<>();
        try {
            Stream<ServiceLoader.Provider<LocalAi>> ais = ServiceLoader.load(LocalAi.class).stream();
            ais.forEach(provider -> {
                try {
                    LocalAi ai = provider.get();
                    map.put(ai.modelName, ai);
                    LOGGER.info("{} has been added", ai);
                } catch (ServiceConfigurationError e) {
                    LOGGER.error("Could not be loaded: {}", e.getMessage(), e);
                }
            });

        } catch (ServiceConfigurationError e) {
            LOGGER.error("no Ais were loaded: {}", e.getMessage(), e);
        }
        AIS = Collections.unmodifiableMap(map);
    }
}
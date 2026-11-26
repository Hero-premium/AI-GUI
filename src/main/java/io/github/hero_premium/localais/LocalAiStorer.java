package io.github.hero_premium.localais;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * this class is a protection layer meant to stop prevent bugs where the user have an old configuration
 * for a model that does no longer exists (e.g. deleted it)
 * @param localAi the local AI
 * @param isAvailable the availability of this model according to ollama
 */
public record LocalAiStorer(@NotNull LocalAi localAi, boolean isAvailable) {
    public LocalAiStorer {
        Objects.requireNonNull(localAi, "localAi must not be null");
    }

    /**
     * @throws ModelUnavailableException if the AI was not available
     * @return the AI, if available
     */
    @Override
    public @NotNull LocalAi localAi() {
        if (!isAvailable) throw new ModelUnavailableException(localAi + " is not available");
        return localAi;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        return o instanceof LocalAiStorer that && Objects.equals(localAi, that.localAi);
    }

    @Override
    public int hashCode() {
        return Objects.hash(localAi);
    }
}

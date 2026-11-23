package io.github.hero_premium.localais;

public class AiUnavailableException extends IllegalStateException {
    public AiUnavailableException() {
        super();
    }

    public AiUnavailableException(String message) {
        super(message);
    }

    public AiUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }

    public AiUnavailableException(Throwable cause) {
        super(cause);
    }
}
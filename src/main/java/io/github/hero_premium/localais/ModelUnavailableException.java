package io.github.hero_premium.localais;

public class ModelUnavailableException extends IllegalStateException {
    public ModelUnavailableException() {
        super();
    }

    public ModelUnavailableException(String message) {
        super(message);
    }

    public ModelUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }

    public ModelUnavailableException(Throwable cause) {
        super(cause);
    }
}
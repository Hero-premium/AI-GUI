package io.github.hero_premium.program.sqlite;

/**
 * to indicate that the value loaded from a column does not match what's expected
 */
public class CorruptDataException extends RuntimeException {
    public CorruptDataException() {
        super();
    }

    public CorruptDataException(String message) {
        super(message);
    }

    public CorruptDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public CorruptDataException(Throwable cause) {
        super(cause);
    }
}

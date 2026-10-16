package io.github.hero_premium.program.sqlite;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SQLiteTest {

    @Test
    void tryCatch() {
        assertThrows(NullPointerException.class, () -> SQLite.tryCatch(null));

        assertDoesNotThrow(() -> SQLite.tryCatch(() -> null));

        String result;
        result = SQLite.tryCatch(() -> returnSomething("Hello"));
        assertEquals("Hello", result);

        result = SQLite.tryCatch(() -> "Hello");
        assertEquals("Hello", result);
    }

    private <T> T returnSomething(T value) {
        return value;
    }
}
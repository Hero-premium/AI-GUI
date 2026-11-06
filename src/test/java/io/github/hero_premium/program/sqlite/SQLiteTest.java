package io.github.hero_premium.program.sqlite;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SQLiteTest {

    @Test
    void tryCatch() {
        assertThrows(NullPointerException.class, () -> SQLiteUtil.tryCatch(null));

        assertDoesNotThrow(() -> SQLiteUtil.tryCatch(() -> null));

        String result;
        result = SQLiteUtil.tryCatch(() -> returnSomething("Hello"));
        assertEquals("Hello", result);

        result = SQLiteUtil.tryCatch(() -> "Hello");
        assertEquals("Hello", result);
    }

    private <T> T returnSomething(T value) {
        return value;
    }
}
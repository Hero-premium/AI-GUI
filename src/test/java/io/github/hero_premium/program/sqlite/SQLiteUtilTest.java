package io.github.hero_premium.program.sqlite;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SQLiteUtilTest {

    @Test
    void tryCatch() {
        assertThrows(NullPointerException.class, () -> SQLiteUtil.tryCatch(null));

        assertDoesNotThrow(() -> SQLiteUtil.tryCatch(() -> null));

        String result = SQLiteUtil.tryCatch(() -> "Hello");
        assertEquals("Hello", result);
    }

}
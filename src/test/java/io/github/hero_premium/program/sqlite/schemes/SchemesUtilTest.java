package io.github.hero_premium.program.sqlite.schemes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SchemesUtilTest {

    @Test
    void addsQuotes() {
        String name = "name";
        name = SchemesUtil.validateName(name);
        assertEquals("\"name\"", name);
    }

    @Test
    void makesNameLowerCase() {
        String name = "nAmE";
        name = SchemesUtil.validateName(name);
        assertEquals("\"name\"", name);
    }

    @Test
    void throwsOnNullName() {
        assertThrows(NullPointerException.class, () -> SchemesUtil.validateName(null));
    }

    @Test
    void throwsOnNotAllowedName() {
        assertThrows(IllegalArgumentException.class, () -> SchemesUtil.validateName("sqlite_hi"));
    }

    @Test
    void throwsOnQuotes() {
        assertThrows(IllegalArgumentException.class, () -> SchemesUtil.validateName("sqlite_hi\""));
    }

    @Test
    void throwsOnBlankName() {
        assertThrows(IllegalArgumentException.class, () -> SchemesUtil.validateName("      "));
    }
}
package io.github.hero_premium.program.sqlite.schemes;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Objects;

class SchemesUtil {

    /**
     *
     * @param name the name to be converted
     * @throws NullPointerException if name was null.
     * @throws IllegalArgumentException if the name starts with "sqlite_" in any case.
     * @throws IllegalArgumentException if the name was blank.
     * @throws IllegalArgumentException if the name contains a quote.
     * @return the name after it's validated (adds quotes, makes it toLowerCase).
     */
    static @NotNull String validateName(String name) {
        Objects.requireNonNull(name, "name cannot be null");
        name = name.toLowerCase(Locale.ROOT);
        if (name.isBlank()) throw new IllegalArgumentException("name cannot be blank");
        if (name.startsWith("sqlite_"))
            throw new IllegalArgumentException("name cannot start with sqlite_");
        if (name.contains("\"")) throw new IllegalArgumentException("name cannot contain a quote");
        return "\"" + name + "\"";
    }

    /**
     * don't instantiate this.
     */
    private SchemesUtil() {
        throw new AssertionError("no io.github.hero_premium.program.sqlite.schemes.SchemesUtil instance for you!");
    }
}

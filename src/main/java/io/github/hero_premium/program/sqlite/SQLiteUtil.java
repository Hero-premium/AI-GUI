package io.github.hero_premium.program.sqlite;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Objects;

class SQLiteUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(SQLiteUtil.class);

     static <T> T tryCatch(@NotNull SQLiteAction<T> action) {
        Objects.requireNonNull(action, "action must not be null");
        try {
            return action.execute();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Contract(pure = true)
     static @NotNull String quoteString(String str) {
        return "\"" + str + "\"";
    }

    /**
     * don't instantiate this.
     */
    private SQLiteUtil() {
        throw new AssertionError("no io.github.hero_premium.program.sqlite.SQLiteUtils instance for you!");
    }
}

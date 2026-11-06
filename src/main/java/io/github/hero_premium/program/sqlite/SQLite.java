package io.github.hero_premium.program.sqlite;

import io.github.hero_premium.program.sqlite.schemes.Table;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 *
 * @deprecated still under construction, don't call it
 */
@Deprecated
public class SQLite {

    public static final String DEFAULT_URL = "jdbc:sqlite:sqlite.db";
    private static final Logger LOGGER = LoggerFactory.getLogger(SQLite.class);

    @NotNull
    private final String url;

    public SQLite(@NotNull Table table) {
        this(table, DEFAULT_URL);
    }

    public SQLite(@NotNull Table table, @NotNull String url) {
        this.url = Objects.requireNonNull(url, "url must not be null");
        openConnection();
        createTable(Objects.requireNonNull(table, "table must not be null"));
    }

    private void openConnection() {
        ConnectionsPool.openConnection(url);
    }

    private void createTable(@NotNull Table table) {
        ConnectionsPool.get(url).ifPresentOrElse(
                connection -> connection.createTable(table),
                () -> LOGGER.error("this connection is closed, cannot create table")
        );
    }

    public static void closeAll() {
        ConnectionsPool.closeAll();
    }
}
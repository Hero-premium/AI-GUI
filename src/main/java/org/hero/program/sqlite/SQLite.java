package org.hero.program.sqlite;

import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class SQLite {

    private static final Logger LOGGER = LoggerFactory.getLogger(SQLite.class);
    private static final Map<String, Connection> connections = new ConcurrentHashMap<>();

    @NotNull
    private final String url;

    public SQLite(@NotNull @Language("SQL") String table) {
        this(table, "jdbc:sqlite:sqlite.db");
    }

    public SQLite(@NotNull @Language("SQL") String table, @NotNull String url) {
        this.url = Objects.requireNonNull(url, "url must not be null");
        tryCatch(() -> {
            open(Objects.requireNonNull(table, "table must not be null"));
            return null;
        });
    }

    protected synchronized void open(String table) throws SQLException {
        if (connections.containsKey(url)) return;
        Connection connection = DriverManager.getConnection(url);
        try (var statement = connection.createStatement()) {
            statement.execute(table);
        }
        connections.put(url, connection);
    }

    private synchronized <T> T runAction(SQLiteAction<T> action) {
        if (connections.get(url) == null) throw new IllegalStateException("connection needs to be opened");
        return tryCatch(action);
    }

    private static <T> T tryCatch(SQLiteAction<T> action) {
        try {
            return action.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void close() {
        Connection connection = connections.get(url);
        if (connection == null) {
            LOGGER.warn("SQLite close() before calling init()");
            return;
        }
        runAction(() -> {
            connection.close();
            return null;
        });
        connections.remove(url);
    }

    public static void closeAll() {
        for (Connection connection : connections.values()) {
            if (connection != null) tryCatch(() -> {
                connection.close();
                return null;
            });
        }
        connections.clear();
    }
}
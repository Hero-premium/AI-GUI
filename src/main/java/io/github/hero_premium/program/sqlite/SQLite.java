package io.github.hero_premium.program.sqlite;

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
            open();
            return null;
        });
        createTable(Objects.requireNonNull(table, "table must not be null"));
    }

    protected synchronized void open() throws SQLException {
        if (connections.containsKey(url)) return;
        Connection connection = DriverManager.getConnection(url);
        connections.put(url, connection);
    }

    private synchronized void createTable(String table) {
        try (var statement = connections.get(url).createStatement()) {
            statement.execute(table);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private synchronized <T> T runAction(SQLiteAction<T> action) {
        if (connections.get(url) == null) throw new IllegalStateException("connection needs to be opened");
        return tryCatch(action);
    }

    public static <T> T tryCatch(@NotNull SQLiteAction<T> action) {
        Objects.requireNonNull(action, "action must not be null");
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
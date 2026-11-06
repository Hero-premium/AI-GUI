package io.github.hero_premium.program.sqlite;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;


class ConnectionsPool {
    private static final Map<String, SQLiteConnection> connections = new ConcurrentHashMap<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionsPool.class);

    static void closeAll() {
        for (SQLiteConnection sqLiteConnection : connections.values()) {
            try {
                sqLiteConnection.close();
            } catch (SQLException e) {
                LOGGER.error("Error while closing SQLiteConnection with URL {}", sqLiteConnection.url, e);
            }
        }
        connections.clear();
    }

    static void openConnection(String url) {
        connections.computeIfAbsent(url, SQLiteConnection::new);
    }

    static @NotNull Optional<SQLiteConnection> get(String url) {
        return Optional.ofNullable(connections.get(url));
    }

}
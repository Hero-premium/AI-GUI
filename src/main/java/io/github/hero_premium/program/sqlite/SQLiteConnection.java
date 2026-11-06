package io.github.hero_premium.program.sqlite;

import io.github.hero_premium.program.sqlite.schemes.Table;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

class SQLiteConnection {

    private final Connection connection;
    final String url;

    SQLiteConnection(String url) {
        this.url = Objects.requireNonNull(url, "url cannot be null");
        try {
            connection = DriverManager.getConnection(url);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    synchronized void createTable(@NotNull Table table) {
        try (var statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS " + table);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    synchronized void close() throws SQLException {
            connection.close();
    }
}

package io.github.hero_premium.program.sqlite;

import io.github.hero_premium.program.sqlite.schemes.TableScheme;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

class SQLiteConnection {

    private static final Logger LOGGER = LoggerFactory.getLogger(SQLiteConnection.class);
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

    synchronized void createTable(@NotNull TableScheme tableScheme) {
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS " + tableScheme);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    synchronized void close() throws SQLException {
        connection.close();
    }

    synchronized void save(@NotNull String tableName, @NotNull Map<TableScheme.Column, Object> values) {
        String command = getCommand(tableName, getColumnNames(values));
        LOGGER.debug(command);
        try (PreparedStatement ps = connection.prepareStatement(command)) {
            int i = 1;
            for (Object toAdd : values.values()) {
                ps.setObject(i++, toAdd);
                LOGGER.debug("added {}", toAdd);
            }
            LOGGER.debug(String.valueOf(ps.executeUpdate()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String @NotNull [] getColumnNames(@NotNull Map<TableScheme.Column, Object> values) {
        return values.keySet().stream()
                .map(TableScheme.Column::name)
                .toList()
                .toArray(new String[0]);
    }

    private @NotNull @Language("sql") String getCommand(@NotNull String tableName, String @NotNull [] columns) {
        StringJoiner columnsNamesJoiner = new StringJoiner(", ", "(", ")");
        StringJoiner valuesJoiner = new StringJoiner(", ", "(", ")");
        for (String column : columns) {
            columnsNamesJoiner.add(column);
            valuesJoiner.add("?");
        }
        return "INSERT INTO %s %s VALUES %s".formatted(tableName, columnsNamesJoiner, valuesJoiner);
    }

    synchronized Object runAction(@Language("sql") String sql) {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            return statement.executeQuery().getObject(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

package io.github.hero_premium.program.sqlite;

import io.github.hero_premium.program.sqlite.schemes.TableScheme;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;

/**
 *
 * still under construction, don't call it
 */
public class SQLiteTable {

    public static final String DEFAULT_URL = "jdbc:sqlite:sqlite.db";
    private static final Logger LOGGER = LoggerFactory.getLogger(SQLiteTable.class);

    @NotNull
    private final String url;
    @NotNull
    public final TableScheme tableScheme;

    public SQLiteTable(@NotNull TableScheme tableScheme) {
        this(tableScheme, DEFAULT_URL);
    }

    public SQLiteTable(@NotNull TableScheme tableScheme, @NotNull String url) {
        this.url = Objects.requireNonNull(url, "url must not be null");
        this.tableScheme = Objects.requireNonNull(tableScheme, "tableScheme must not be null");
        openConnection();
        createTable(tableScheme);
    }

    private void openConnection() {
        ConnectionsPool.openConnection(url);
    }

    private void createTable(@NotNull TableScheme tableScheme) {
        ConnectionsPool.get(url).ifPresentOrElse(
                connection -> connection.createTable(tableScheme),
                () -> LOGGER.error("this connection is closed, cannot create table")
        );
    }

    private void save(@NotNull SQLiteConnection sqLiteConnection, @NotNull Map<TableScheme.Column, Object> toAddToColumns) {
        for (Map.Entry<TableScheme.Column, Object> columnsName : toAddToColumns.entrySet()) {
            TableScheme.Column column = columnsName.getKey();
            Object value = columnsName.getValue();
            if (!column.value().type.isInstance(value))
                throw new IllegalArgumentException("value type " + value.getClass() + " is not the expected type" + column.value().type);
            if (tableScheme.columns.stream()
                    .noneMatch(columns -> columns.name().equals(column.name())))
                throw new IllegalArgumentException("column name " + columnsName + " does not exist");
        }
        sqLiteConnection.save(tableScheme.name, toAddToColumns);
    }

    public void save(Map<TableScheme.Column, Object> toAddToColumns) {
        ConnectionsPool.get(url).ifPresentOrElse(
                connection -> save(connection,
                        Objects.requireNonNull(toAddToColumns, "columnsNames must not be null")),
                () -> LOGGER.error("this connection is closed, cannot save to table"));
    }

    // used for tests, temporarily, do not call
    Object runAction(@NotNull @Language("sql") String sql) {
        return ConnectionsPool.get(url).map(connection -> connection.runAction(sql))
                .orElseGet(() -> {
                    LOGGER.error("Connection not found");
                    return null;
                });
    }

    /**
     * closes all live connections, meant to be called before closing the application
     */
    public static void closeAll() {
        ConnectionsPool.closeAll();
    }
}
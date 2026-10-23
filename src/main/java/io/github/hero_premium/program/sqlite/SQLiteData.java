package io.github.hero_premium.program.sqlite;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class SQLiteData {


    public record Table(String name, Column... columns) {
        public Table {
            Objects.requireNonNull(columns);
            Objects.requireNonNull(name);
        }

        @Override
        public @NotNull String toString() {
            StringBuilder builder = new StringBuilder();
            for (Column column : columns) {
                builder.append(column.toString()).append(", ");
            }
            return name + " ( " + builder.substring(0, builder.length() - 2) + " )";
        }
    }

    public record Column(String name, ColumnType value) {

        @Override
        public @NotNull String toString() {
            return name + " " + value;
        }


    }

    public enum ColumnType {
        INTEGER,
        REAL,
        TEXT,
        BLOB,
        NUMERIC
    }

    /**
     * don't instantiate this
     */
    private SQLiteData() {
        throw new AssertionError("no io.github.hero_premium.program.sqlite.SQLiteData instance for you!");
    }
}

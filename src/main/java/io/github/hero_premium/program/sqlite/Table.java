package io.github.hero_premium.program.sqlite;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class Table {
    public final String name;
    private final Set<Column> columns;

    public static TableBuilder tableBuilder() {
        return new TableBuilder();
    }

    /// use {@link Table#tableBuilder()}
    private Table(String name, Set<Column> columns) {
        this.columns = Objects.requireNonNull(columns);
        this.name = Objects.requireNonNull(name);
    }

    @Override
    public @NotNull String toString() {
        StringJoiner sj = new StringJoiner(", ", "(", ")");
        for (Column column : columns) {
            sj.add(column.toString());
        }
        return String.join(" ", name, sj.toString());
    }

    public String name() {
        return name;
    }

    public Set<Column> columns() {
        return columns;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Table) obj;
        return Objects.equals(this.name, that.name) &&
                Objects.equals(this.columns, that.columns);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, columns);
    }


    public static class TableBuilder {
        /// use {@link Table#tableBuilder()}
        private TableBuilder() {
        }

        private String name;
        private final Set<Column> columns = new LinkedHashSet<>();

        public TableBuilder name(@NotNull String name) {
            if (this.name != null) throw new IllegalStateException("name already set");
            this.name = Objects.requireNonNull(name, "name must not be null");
            return this;
        }

        public TableBuilder column(@NotNull Column column) {
            if (!columns.add(Objects.requireNonNull(column, "column must not be null"))) {
                throw new IllegalArgumentException("this column has already been added");
            }
            return this;
        }

        public Table build() {
            if (name == null) {
                throw new IllegalStateException("name must be provided before build");
            }
            if (columns.isEmpty()) {
                throw new IllegalStateException("columns must not be empty");
            }
            return new Table(name, Collections.unmodifiableSet(columns));
        }
    }

    public record Column(String name, ColumnType value) {
        @Override
        public @NotNull String toString() {
            return String.join(" ", name, value.toString());
        }
    }

    public enum ColumnType {
        INTEGER,
        REAL,
        TEXT,
        BLOB,
        NUMERIC
    }
}
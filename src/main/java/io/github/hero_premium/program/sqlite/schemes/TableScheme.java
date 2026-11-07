package io.github.hero_premium.program.sqlite.schemes;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class TableScheme {
    public final String name;
    public final Set<Column> columns;

    @Contract(" -> new")
    public static @NotNull TableBuilder tableBuilder() {
        return new TableBuilder();
    }

    /// use {@link TableScheme#tableBuilder()}
    private TableScheme(String name, Set<Column> columns) {
        this.columns = Objects.requireNonNull(columns);
        this.name = SchemesUtil.validateName(name);
    }

    @Override
    public @NotNull String toString() {
        StringJoiner sj = new StringJoiner(", ", "(", ")");
        for (Column column : columns) {
            sj.add(column.toString());
        }
        return String.join(" ", name, sj.toString());
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (TableScheme) obj;
        return Objects.equals(this.name, that.name) &&
                Objects.equals(this.columns, that.columns);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, columns);
    }


    public static class TableBuilder {
        /// use {@link TableScheme#tableBuilder()}
        private TableBuilder() {
        }

        private String name;
        private final Set<Column> columns = new LinkedHashSet<>();
        private boolean used = false;

        public TableBuilder name(@NotNull String name) {
            if (this.name != null) throw new IllegalStateException("name already set");
            this.name = name;
            return this;
        }

        public TableBuilder column(String name, ColumnType value) {
            Column column = new Column(name, value);
            if (!columns.add(Objects.requireNonNull(column, "column must not be null"))) {
                throw new IllegalArgumentException("this column has already been added");
            }
            return this;
        }

        public TableScheme build() {
            if (used) {
                throw new IllegalStateException("this builder cannot be reused");
            }
            if (name == null) {
                throw new IllegalStateException("name must be provided before build");
            }
            if (columns.isEmpty()) {
                throw new IllegalStateException("columns must not be empty");
            }
            used = true;
            return new TableScheme(name, Collections.unmodifiableSet(columns));
        }
    }

    public record Column(@NotNull String name, @NotNull ColumnType value) {
        public Column {
            name = SchemesUtil.validateName(name);
            Objects.requireNonNull(value, "value must not be null");
        }

        @Override
        public @NotNull String toString() {
            return String.join(" ", name, value.toString());
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (Column) obj;
            return Objects.equals(this.name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }
    }

    public enum ColumnType {
        INTEGER(Long.class),
        REAL(Double.class),
        TEXT(String.class),
        BLOB(byte[].class);

        public final Class<?> type;

        ColumnType(Class<?> type) {
            this.type = type;
        }
    }
}
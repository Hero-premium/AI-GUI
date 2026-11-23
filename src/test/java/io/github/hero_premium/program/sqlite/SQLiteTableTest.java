package io.github.hero_premium.program.sqlite;

import io.github.hero_premium.program.sqlite.schemes.TableScheme;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SQLiteTableTest {

    @Test
    void save() {
        SQLiteTable sqLiteTable = new SQLiteTable(TableScheme.tableBuilder()
                .name("Test")
                .column("ID", TableScheme.ColumnType.INTEGER)
                .column("NAME", TableScheme.ColumnType.TEXT)
                .build(), "jdbc:sqlite::memory:");
        sqLiteTable.save(Map.of(sqLiteTable.tableScheme.columns.get(1), "hi"));
        String string = sqLiteTable.loadColumn(sqLiteTable.tableScheme.columns.get(1), String.class).getFirst();
        assertEquals("hi", string);
    }

    @Test
    void load() {
        SQLiteTable sqLiteTable = new SQLiteTable(TableScheme.tableBuilder()
                .name("Test")
                .column("ID", TableScheme.ColumnType.INTEGER)
                .column("NAME", TableScheme.ColumnType.TEXT)
                .build(), Constants.TO_MEMORY_URL);

        for (String s : Arrays.asList("hi", "i", "hope", "you")) {
            sqLiteTable.save(Map.of(sqLiteTable.tableScheme.columns.get(1), s));
        }

        List<String> strings = sqLiteTable.loadColumn(sqLiteTable.tableScheme.columns.get(1), String.class);
        assertEquals("hi", strings.getFirst());
        assertEquals("i", strings.get(1));
        assertEquals("hope", strings.get(2));
        assertEquals("you", strings.get(3));
    }

    @Test
    void throwsException() {
        SQLiteTable sqLiteTable = new SQLiteTable(TableScheme.tableBuilder()
                .name("Test")
                .column("ID", TableScheme.ColumnType.INTEGER)
                .column("NAME", TableScheme.ColumnType.TEXT)
                .build(), Constants.TO_MEMORY_URL);
        assertThrows(IllegalArgumentException.class, () ->
                sqLiteTable.loadColumn(sqLiteTable.tableScheme.columns.getFirst(), String.class));
    }
}
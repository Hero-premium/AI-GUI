package io.github.hero_premium.program.sqlite;

import io.github.hero_premium.program.sqlite.schemes.TableScheme;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SQLiteTableTest {
    @Test
    void save() {
        SQLiteTable sqLiteTable = new SQLiteTable(TableScheme.tableBuilder()
                .name("Test")
                .column("ID", TableScheme.ColumnType.INTEGER)
                .column("NAME", TableScheme.ColumnType.TEXT)
                .build(), "jdbc:sqlite::memory:");
        sqLiteTable.save(Map.of((TableScheme.Column) sqLiteTable.tableScheme.columns.toArray()[1], "hi"));
        String string = (String) sqLiteTable.runAction("SELECT \"name\" FROM \"Test\"");
        assertEquals("hi", string);
    }
}
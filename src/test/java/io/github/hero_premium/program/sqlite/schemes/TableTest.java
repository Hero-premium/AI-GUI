package io.github.hero_premium.program.sqlite.schemes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TableTest {

    @Test
    void createTable() {
        Table table;
        table = Table.tableBuilder()
                .name("hi")
                .column("cat", Table.ColumnType.TEXT)
                .build();
        assertEquals("\"hi\" (\"cat\" TEXT)", table.toString());
    }

    @Test
    void moreThanOneColumnWithTheSameName() {
        assertThrows(IllegalArgumentException.class, () -> Table.tableBuilder()
                .name("hi")
                .column("cat", Table.ColumnType.TEXT)
                .column("cat", Table.ColumnType.INTEGER)
                .build());
    }

    @Test
    void moreThanOneColumn() {
        Table table = Table.tableBuilder()
                .name("hi")
                .column("cat", Table.ColumnType.TEXT)
                .column("dog", Table.ColumnType.INTEGER)
                .build();
        assertEquals("\"hi\" (\"cat\" TEXT, \"dog\" INTEGER)", table.toString());
    }

    @Test
    void doubleBuild() {
        Table.TableBuilder builder = Table.tableBuilder();
        builder.name("hi")
                .column("cat", Table.ColumnType.TEXT)
                .column("dog", Table.ColumnType.INTEGER)
                .build();
        assertThrows(IllegalStateException.class, builder::build);
    }

    @Test
    void buildWithNoName() {
        assertThrows(IllegalStateException.class, () -> Table.tableBuilder()
                .column("hi", Table.ColumnType.INTEGER)
                .build());
    }

    @Test
    void buildWithNoColumns() {
        assertThrows(IllegalStateException.class, () -> Table.tableBuilder()
                .name("bat is my pet bats name")
                .build());
    }
}
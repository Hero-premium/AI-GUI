package io.github.hero_premium.program.sqlite;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TableTest {

    @Test
    void createTable() {
        Table table;
        table = Table.tableBuilder()
                .name("hi")
                .column(new Table.Column("cat", Table.ColumnType.TEXT))
                .build();
        assertEquals("hi (cat TEXT)", table.toString());
    }

    @Test
    void moreThanOneColumnWithTheSameName() {
        assertThrows(IllegalArgumentException.class, () -> Table.tableBuilder()
                .name("hi")
                .column(new Table.Column("cat", Table.ColumnType.TEXT))
                .column(new Table.Column("cat", Table.ColumnType.TEXT))
                .build());
    }
    @Test
    void moreThanOneColumn() {
        Table table = Table.tableBuilder()
                .name("hi")
                .column(new Table.Column("cat", Table.ColumnType.TEXT))
                .column(new Table.Column("dog", Table.ColumnType.INTEGER))
                .build();
        assertEquals("hi (cat TEXT, dog INTEGER)", table.toString());
    }

}
package io.github.hero_premium.program.sqlite.schemes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TableSchemeTest {

    @Test
    void createTable() {
        TableScheme tableScheme;
        tableScheme = TableScheme.tableBuilder()
                .name("hi")
                .column("cat", TableScheme.ColumnType.TEXT)
                .build();
        assertEquals("\"hi\" (\"cat\" TEXT)", tableScheme.toString());
    }

    @Test
    void moreThanOneColumnWithTheSameName() {
        assertThrows(IllegalArgumentException.class, () -> TableScheme.tableBuilder()
                .name("hi")
                .column("cat", TableScheme.ColumnType.TEXT)
                .column("cat", TableScheme.ColumnType.INTEGER)
                .build());
    }

    @Test
    void moreThanOneColumn() {
        TableScheme tableScheme = TableScheme.tableBuilder()
                .name("hi")
                .column("cat", TableScheme.ColumnType.TEXT)
                .column("dog", TableScheme.ColumnType.INTEGER)
                .build();
        assertEquals("\"hi\" (\"cat\" TEXT, \"dog\" INTEGER)", tableScheme.toString());
    }

    @Test
    void doubleBuild() {
        TableScheme.TableBuilder builder = TableScheme.tableBuilder();
        builder.name("hi")
                .column("cat", TableScheme.ColumnType.TEXT)
                .column("dog", TableScheme.ColumnType.INTEGER)
                .build();
        assertThrows(IllegalStateException.class, builder::build);
    }

    @Test
    void buildWithNoName() {
        assertThrows(IllegalStateException.class, () -> TableScheme.tableBuilder()
                .column("hi", TableScheme.ColumnType.INTEGER)
                .build());
    }

    @Test
    void buildWithNoColumns() {
        assertThrows(IllegalStateException.class, () -> TableScheme.tableBuilder()
                .name("bat is my pet bats name")
                .build());
    }
}
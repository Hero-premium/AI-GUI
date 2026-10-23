package io.github.hero_premium.program.sqlite;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SQLiteDataTest {

    @Test
    void createTable() {
        SQLiteData.Table table;
        table = new SQLiteData.Table("hi",
                new SQLiteData.Column("cat", SQLiteData.ColumnType.TEXT));
        assertEquals("hi ( cat TEXT )", table.toString());
    }

    @Test
    void moreThanOneColumn() {
        SQLiteData.Table table;
        table = new SQLiteData.Table("hi",
                new SQLiteData.Column("cat", SQLiteData.ColumnType.TEXT),
                new SQLiteData.Column("cat", SQLiteData.ColumnType.TEXT));
        assertEquals("hi ( cat TEXT, cat TEXT )", table.toString());
    }

}
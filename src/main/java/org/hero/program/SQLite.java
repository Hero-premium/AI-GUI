package org.hero.program;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLite {
    private static Connection connection;


    private SQLite() {
        throw new AssertionError("No org.hero.program.SQLite instance for you!");
    }

    static void init() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:sqlite.db");
            connection.createStatement().execute("CREATE TABLE IF NOT EXISTS conversations (id INTEGER PRIMARY KEY , title TEXT)");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
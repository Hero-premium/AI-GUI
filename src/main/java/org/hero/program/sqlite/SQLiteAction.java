package org.hero.program.sqlite;

import java.sql.SQLException;

@FunctionalInterface
public interface SQLiteAction<T> {

    T execute() throws SQLException;

}
package org.hero.program.sqlite;

import java.sql.SQLException;

@FunctionalInterface
public interface SQLiteAction<T> {

    /**
     * meant to carry SQL methods as most of them throw SQLException
     *
     * @return the method's return, expect null if the method return type is void
     * @throws SQLException when the method throws it for various reasons
     */
    T execute() throws SQLException;

}
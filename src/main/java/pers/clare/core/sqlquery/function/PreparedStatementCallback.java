package pers.clare.core.sqlquery.function;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@FunctionalInterface
public interface PreparedStatementCallback {
    void accept(PreparedStatement ps) throws SQLException;
}

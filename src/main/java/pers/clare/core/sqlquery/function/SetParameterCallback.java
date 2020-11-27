package pers.clare.core.sqlquery.function;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@FunctionalInterface
public interface SetParameterCallback<T> {
    void accept(PreparedStatement ps, T parameter) throws SQLException;
}

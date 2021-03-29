package pers.clare.core.sqlquery.util;

import java.sql.*;

public class PreparedStatementUtil {

    public static Statement executeInsert(
            Connection conn
            , String sql
            , Object... parameters
    ) throws SQLException {
        if (parameters.length == 0) {
            Statement statement = conn.createStatement();
            statement.execute(sql, Statement.RETURN_GENERATED_KEYS);
            return statement;
        } else {
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            int i = 1;
            for (Object value : parameters) {
                ps.setObject(i++, value);
            }
            ps.executeUpdate();
            return ps;
        }
    }

    public static int setValue(
            PreparedStatement ps
            , Object ...parameters
    ) throws SQLException {
        int index = 1;
        if (parameters == null || parameters.length == 0) return index;
        for (Object value : parameters) {
            ps.setObject(index++, value);
        }
        return index;
    }
}

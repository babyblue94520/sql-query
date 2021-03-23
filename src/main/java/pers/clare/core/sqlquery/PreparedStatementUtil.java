package pers.clare.core.sqlquery;

import pers.clare.core.sqlquery.page.Pagination;

import java.sql.*;

public class PreparedStatementUtil {

    public static ResultSet query(
            Connection conn
            , String sql
            , Object... parameters
    ) throws SQLException {
        if (parameters.length == 0) {
            return conn.createStatement().executeQuery(sql);
        } else {
            PreparedStatement ps = conn.prepareStatement(sql);
            setValue(ps, parameters);
            return ps.executeQuery();
        }
    }

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

    public static int execute(
            Connection conn
            , String sql
            , Object... parameters
    ) throws SQLException {
        if (parameters.length == 0) {
            return conn.createStatement().executeUpdate(sql);
        } else {
            PreparedStatement ps = conn.prepareStatement(sql);
            setValue(ps, parameters);
            return ps.executeUpdate();
        }
    }

    public static PreparedStatement createInsert(
            Connection conn
            , String sql
            , Object... parameters
    ) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        if (parameters != null) {
            int i = 1;
            for (Object value : parameters) {
                ps.setObject(i++, value);
            }
        }
        return ps;
    }

    public static PreparedStatement create(
            Connection conn
            , String sql
            , Object... parameters
    ) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(sql);
        setValue(ps, parameters);
        return ps;
    }

    public static PreparedStatement create(
            Connection conn
            , String sql
            , Pagination pagination
            , Object... parameters
    ) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(SQLUtil.buildPaginationSQL(pagination, sql));
        int lastIndex = setValue(ps, parameters);
        ps.setInt(lastIndex++, pagination.getPage() * pagination.getSize());
        ps.setInt(lastIndex, pagination.getSize());
        return ps;
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

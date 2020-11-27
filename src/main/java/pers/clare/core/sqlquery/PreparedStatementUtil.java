package pers.clare.core.sqlquery;

import org.springframework.data.domain.Pageable;

import java.sql.*;

public class PreparedStatementUtil {

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
            , Pageable pageable
            , Object... parameters
    ) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(SQLUtil.buildPaginationSQL(pageable, sql));
        int lastIndex = setValue(ps, parameters);
        ps.setInt(lastIndex++, pageable.getPageNumber() * pageable.getPageSize());
        ps.setInt(lastIndex, pageable.getPageSize());
        return ps;
    }

    public static int setValue(
            PreparedStatement ps
            , Object[] parameters
    ) throws SQLException {
        int index = 1;
        if (parameters == null || parameters.length == 0) return index;
        for (Object value : parameters) {
            ps.setObject(index++, value);
        }
        return index;
    }
}

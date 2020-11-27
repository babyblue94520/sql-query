package pers.clare.core.sqlquery;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.sql.*;
import java.util.*;

public interface SQLQueryPSUtil {

    public static PreparedStatement createInsert(
            Connection conn
            , SQLQuery query
    ) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(query.toString(), Statement.RETURN_GENERATED_KEYS);
        setValue(ps, query.values);
        return ps;
    }

    public static PreparedStatement create(
            Connection conn
            , SQLQuery query
    ) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(query.toString());
        setValue(ps, query.values);
        return ps;
    }

    public static PreparedStatement create(
            Connection conn
            , SQLQuery query
            , Sort sort
    ) throws SQLException {
        String sql = query.toString() + SQLUtil.toOrder(sort);
        PreparedStatement ps = conn.prepareStatement(sql);
        setValue(ps, query.values);
        return ps;
    }

    public static PreparedStatement create(
            Connection conn
            , String sql
            , SQLQuery query
    ) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(sql);
        setValue(ps, query.values);
        return ps;
    }

    public static PreparedStatement create(
            Connection conn
            , String sql
            , SQLQuery query
            , Pageable pageable
    ) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(SQLUtil.buildPaginationSQL(pageable, sql));
        int lastIndex = SQLQueryPSUtil.setValue(ps, query.values);
        ps.setInt(lastIndex++, pageable.getPageNumber() * pageable.getPageSize());
        ps.setInt(lastIndex, pageable.getPageSize());
        return ps;
    }


    public static int setValue(
            PreparedStatement ps
            , Object[] parameters
    ) throws SQLException {
        Object value;
        int index = 1;
        for (int i = 0, l = parameters.length; i < l; i++) {
            value = parameters[i];
            if (value == null) {
                ps.setObject(index++, value);
                continue;
            }
            Class<?> valueClass = value.getClass();
            if (valueClass.isArray()) {
                Object[] array = (Object[]) value;
                for (int j = 0, jl = array.length; j < jl; j++) {
                    ps.setObject(index++, array[j]);
                }
            } else if (Collection.class.isAssignableFrom(valueClass)) {
                Collection<Object> collections = (Collection<Object>) value;
                for (Object obj : collections) {
                    ps.setObject(index++, obj);
                }
            } else {
                ps.setObject(index++, value);
            }
        }
        return index;
    }
}

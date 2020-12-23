package pers.clare.core.sqlquery;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.sql.*;
import java.util.*;

public class ResultSetUtil {

    public static String[] getNames(ResultSet rs) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        int count = rsmd.getColumnCount();
        int i;
        String[] names = new String[count];
        for (i = 0; i < count; ) {
            names[i] = rsmd.getColumnLabel(++i);
        }
        return names;
    }

    public static <T> T to(Class<T> clazz, ResultSet rs) throws SQLException {
        if (rs.next()) {
            return rs.getObject(1, clazz);
        }
        return null;
    }

    public static Map<String, Object> toMap(ResultSet rs) throws SQLException {
        if (rs.next()) return toMap(rs, getNames(rs));
        return null;
    }


    private static Map<String, Object> toMap(ResultSet rs, String[] names) throws SQLException {
        Map<String, Object> map = new HashMap<>();
        int i = 1;
        for (String name : names) {
            map.put(name, rs.getObject(i++));
        }
        return map;
    }

    public static <T> Map<String, T> toMap(Class<T> valueClass, ResultSet rs) throws SQLException {
        if (rs.next()) return toMap(valueClass, rs, getNames(rs));
        return null;
    }

    private static <T> Map<String, T> toMap(Class<T> valueClass, ResultSet rs, String[] names) throws SQLException {
        Map<String, T> map = new HashMap<>();
        int i = 1;
        for (String name : names) {
            map.put(name, rs.getObject(i++, valueClass));
        }
        return map;
    }

    public static <T> Set<T> toSet(Class<T> clazz, ResultSet rs) throws SQLException {
        Set<T> result = new HashSet<>();
        while (rs.next()) {
            result.add(rs.getObject(1, clazz));
        }
        return result;
    }

    public static List<Map<String, Object>> toList(ResultSet rs) throws SQLException {
        String[] names = getNames(rs);
        List<Map<String, Object>> list = new ArrayList<>();
        while (rs.next()) {
            list.add(toMap(rs, names));
        }
        return list;
    }

    public static <T> List<T> toList(Class<T> clazz, ResultSet rs) throws SQLException {
        List<T> result = new ArrayList<>();
        while (rs.next()) {
            result.add(rs.getObject(1, clazz));
        }
        return result;
    }
}

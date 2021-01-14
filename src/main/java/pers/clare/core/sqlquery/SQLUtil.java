package pers.clare.core.sqlquery;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import pers.clare.core.sqlquery.exception.SQLQueryException;

import java.lang.reflect.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;


public class SQLUtil {
    private static final boolean camelCase = true;
    private static final char[] get = new char[]{'g', 'e', 't'};

    SQLUtil() {
    }

    public static String buildTotalSQL(String sql) {
        return new StringBuilder("select count(*) from(").append(sql)
                .append(")t")
                .toString();
    }

    public static String buildPaginationSQL(
            Pageable pageable
            , String sql
    ) {
        StringBuilder sb = new StringBuilder(sql);
        return toOrder(sb, pageable.getSort())
                .append(" limit ?,?")
                .toString();
    }

    /**
     * 取得排序字串.
     *
     * @param sort the sort
     * @return the order
     */
    public static String toOrder(Sort sort) {
        if (sort == null) return "";
        return toOrder(new StringBuilder(), sort).toString();
    }

    public static StringBuilder toOrder(StringBuilder sb, Sort sort) {
        if (sort == null) return sb;
        Order order;
        Iterator<Order> iterator = sort.iterator();
        while (iterator.hasNext()) {
            order = iterator.next();
            turnCamelCase(sb, order.getProperty())
                    .append(' ')
                    .append(order.getDirection())
                    .append(',');
        }
        if (sb.length() > 0) {
            sb.delete(sb.length() - 1, sb.length())
                    .append(" order by ");
        }
        return sb;
    }

    private static StringBuilder turnCamelCase(
            StringBuilder sb
            , String str
    ) {
        char[] bs = str.toCharArray();
        int l = bs.length, c = -1;
        char[] nb = new char[l * 2];
        char b;
        if (camelCase) {
            for (int i = 0; i < l; i++) {
                b = bs[i];
                // 移除 ';' 結束字元
                if (b == 59) continue;
                // 紀錄最後的 '.'
                if (b == 46) {
                    c = -1;
                    continue;
                }
                // 駝峰換底線
                if (b > 64 && b < 91) {
                    nb[++c] = '_';
                    b = Character.toLowerCase(b);
                }
                nb[++c] = b;
            }
        } else {
            for (int i = 0; i < l; i++) {
                b = bs[i];
                // 移除 ';' 結束字元
                if (b == 59) continue;
                // 紀錄最後的 '.'
                if (b == 46) {
                    c = -1;
                    continue;
                }
                nb[++c] = b;
            }
        }
        sb.append(nb, 1, c);
        return sb;
    }

    public static String convert(String name) {
        int l = name.length();
        char[] cs = name.toCharArray();
        char[] rs = new char[l * 2 + 2];
        rs[0] = '`';
        char c = cs[0];
        rs[1] = (c < 97 ? Character.toLowerCase(c) : c);
        int index = 2;
        for (int i = 1; i < l; i++) {
            c = cs[i];
            if (c < 97) {
                rs[index++] = '_';
                rs[index++] = Character.toLowerCase(c);
            } else {
                rs[index++] = c;
            }
        }
        rs[index++] = '`';
        return new String(rs, 0, index);
    }

    public static String toGetterName(String name) {
        int l = name.length();
        char[] rs = new char[l + 3];
        System.arraycopy(get, 0, rs, 0, 3);
        name.getChars(0, l, rs, 3);
        rs[3] = Character.toUpperCase(rs[3]);
        return new String(rs);
    }

    public static String setValue(SQLQueryBuilder sqlQueryBuilder, Field[] fields, Object[] parameters) {
        SQLQuery sqlQuery = sqlQueryBuilder.build();
        for (int i = 0; i < parameters.length; i++) {
            sqlQuery.value(fields[i].getName(), parameters[i]);
        }
        return sqlQuery.toString();
    }

    public static <T> String setValue(SQLQueryBuilder sqlQueryBuilder, Field[] fields, T entity) {
        try {
            SQLQuery sqlQuery = sqlQueryBuilder.build();
            for (Field f : fields) {
                sqlQuery.value(f.getName(), f.get(entity));
            }
            return sqlQuery.toString();
        } catch (Exception e) {
            throw new SQLQueryException(e.getMessage(), e);
        }
    }

    public static <T> String setValue2(SQLQueryBuilder sqlQueryBuilder, Field[] fields, Field[] keyFields, T entity) {
        try {
            SQLQuery sqlQuery = sqlQueryBuilder.build();
            for (Field f : fields) {
                sqlQuery.value(f.getName(), f.get(entity));
            }
            for (Field f : keyFields) {
                sqlQuery.value(f.getName(), f.get(entity));
            }
            return sqlQuery.toString();
        } catch (Exception e) {
            throw new SQLQueryException(e.getMessage(), e);
        }
    }

    public static <T> T toInstance(Map<Integer, Constructor<T>> constructorMap, ResultSet rs) throws Exception {
        if (rs.next()) {
            return buildInstance(findConstructor(constructorMap, rs.getMetaData()), rs);
        }
        return null;
    }

    public static <T> Set<T> toSetInstance(Map<Integer, Constructor<T>> constructorMap, ResultSet rs) throws Exception {
        Set<T> result = new HashSet<>();
        Constructor<T> constructor = findConstructor(constructorMap, rs.getMetaData());
        while (rs.next()) {
            result.add(buildInstance(constructor, rs));
        }
        return result;
    }

    public static <T> List<T> toInstances(Map<Integer, Constructor<T>> constructorMap, ResultSet rs) throws Exception {
        List<T> list = new ArrayList<>();
        Constructor<T> constructor = findConstructor(constructorMap, rs.getMetaData());
        while (rs.next()) {
            list.add(buildInstance(constructor, rs));
        }
        return list;
    }

    private static <T> T buildInstance(Constructor<T> constructor, ResultSet rs) throws Exception {
        int l = constructor.getParameterCount();
        Parameter[] parameters = constructor.getParameters();
        Object[] values = new Object[l];
        for (int i = 0; i < l; i++) {
            values[i] = rs.getObject(i + 1, parameters[i].getType());
        }
        return constructor.newInstance(values);
    }

    private static <T> Constructor<T> findConstructor(Map<Integer, Constructor<T>> constructorMap, ResultSetMetaData metaData) throws Exception {
        Constructor<T> constructor = constructorMap.get(metaData.getColumnCount());
        if (constructor != null) return constructor;
        StringBuilder columns = new StringBuilder("(");
        for (int i = 0, l = metaData.getColumnCount(); i < l; i++) {
            columns.append(metaData.getColumnName(i + 1));
            columns.append(',');
        }
        columns.replace(columns.length() - 1, columns.length(), ")");
        throw new Exception("Cannot find constructor" + columns);
    }
}

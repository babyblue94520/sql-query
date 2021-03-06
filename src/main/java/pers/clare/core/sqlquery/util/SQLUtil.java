package pers.clare.core.sqlquery.util;

import pers.clare.core.sqlquery.SQLQuery;
import pers.clare.core.sqlquery.SQLQueryBuilder;
import pers.clare.core.sqlquery.SQLStore;
import pers.clare.core.sqlquery.exception.SQLQueryException;
import pers.clare.core.sqlquery.function.FieldGetHandler;
import pers.clare.core.sqlquery.function.FieldSetHandler;
import pers.clare.core.sqlquery.naming.NamingStrategy;
import pers.clare.core.sqlquery.page.Pagination;
import pers.clare.core.sqlquery.page.Sort;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.*;


public class SQLUtil {

    SQLUtil() {
    }

    public static String buildTotalSQL(String sql) {
        return "select count(*) from(" + sql + ")t";
    }

    public static String buildPaginationSQL(
            Pagination pagination
            , String sql
    ) {
        StringBuilder sb = new StringBuilder(sql);
        appendPaginationSQL(sb, pagination);
        return sb.toString();
    }

    public static String buildSortSQL(
            Sort sort
            , String sql
    ) {
        StringBuilder sb = new StringBuilder(sql);
        appendSortSQL(sb, sort.getSorts());
        return sb.toString();
    }

    public static void appendPaginationSQL(
            StringBuilder sql
            , Pagination pagination
    ) {
        appendSortSQL(sql, pagination.getSorts());
        sql.append(" limit ")
                .append(pagination.getSize() * pagination.getPage())
                .append(',')
                .append(pagination.getSize());
    }

    public static void appendSortSQL(StringBuilder sql, String[] sorts) {
        if (sorts == null) return;
        sql.append(" order by ");
        for (String sort : sorts) {
            if (sort == null || sort.length() == 0) continue;
            NamingStrategy.sortTurnCamelCase(sql, sort);
            sql.append(',');
        }
        sql.delete(sql.length() - 1, sql.length());
    }

    public static void appendValue(
            StringBuilder sb
            , Object value
    ) {
        if (value instanceof String) {
            sb.append('\'');
            char[] cs = ((String) value).toCharArray();
            for (char c : cs) {
                sb.append(c);
                if (c == '\'') sb.append('\'');
            }
            sb.append('\'');
        } else {
            sb.append(value);
        }
    }

    /**
     * appendIn
     * 依陣列數量，動態產生 (?,?,?,?) or ((?,?),(?,?))
     *
     * @param sb
     * @param value
     */
    public static void appendInValue(
            StringBuilder sb
            , Object value
    ) {
        Class<?> valueClass = value.getClass();
        if (valueClass.isArray()) {
            sb.append('(');
            if (value instanceof Object[]) {
                Object[] vs = (Object[]) value;
                if (vs.length == 0) throw new IllegalArgumentException("SQL WHERE IN doesn't empty value");
                for (Object v : vs) appendInValue(sb, v);
            } else if (value instanceof int[]) {
                int[] vs = (int[]) value;
                if (vs.length == 0) throw new IllegalArgumentException("SQL WHERE IN doesn't empty value");
                for (int v : vs) appendInValue(sb, v);
            } else if (value instanceof long[]) {
                long[] vs = (long[]) value;
                if (vs.length == 0) throw new IllegalArgumentException("SQL WHERE IN doesn't empty value");
                for (long v : vs) appendInValue(sb, v);
            } else if (value instanceof char[]) {
                char[] vs = (char[]) value;
                if (vs.length == 0) throw new IllegalArgumentException("SQL WHERE IN doesn't empty value");
                for (char v : vs) appendInValue(sb, v);
            }
            sb.deleteCharAt(sb.length() - 1).append(')');
        } else if (Collection.class.isAssignableFrom(valueClass)) {
            Collection<Object> vs = (Collection<Object>) value;
            if (vs.size() == 0) throw new IllegalArgumentException("SQL WHERE IN doesn't empty value");
            sb.append('(');
            for (Object v : vs) appendInValue(sb, v);
            sb.deleteCharAt(sb.length() - 1).append(')');
        } else {
            SQLUtil.appendValue(sb, value);
        }
        sb.append(',');
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
            throw new SQLQueryException(e);
        }
    }

    public static <T> void setValue(SQLQuery sqlQuery, Map<String, FieldGetHandler> fields, T entity) {
        try {
            for (Map.Entry<String, FieldGetHandler> entry : fields.entrySet()) {
                sqlQuery.value(entry.getKey(), entry.getValue().apply(entity));
            }
        } catch (Exception e) {
            throw new SQLQueryException(e);
        }
    }

    public static <T> T toInstance(SQLStore<T> sqlStore, ResultSet rs) throws Exception {
        FieldSetHandler[] fields = toFields(sqlStore.getFieldSetMap(), rs.getMetaData());
        if (rs.next()) {
            return buildInstance(sqlStore.getConstructor(), fields, rs);
        }
        return null;
    }

    public static <T> Set<T> toSetInstance(SQLStore<T> sqlStore, ResultSet rs) throws Exception {
        Set<T> result = new HashSet<>();
        FieldSetHandler[] fields = toFields(sqlStore.getFieldSetMap(), rs.getMetaData());
        while (rs.next()) {
            result.add(buildInstance(sqlStore.getConstructor(), fields, rs));
        }
        return result;
    }

    public static <T> List<T> toInstances(SQLStore<T> sqlStore, ResultSet rs) throws Exception {
        List<T> list = new ArrayList<>();
        FieldSetHandler[] fields = toFields(sqlStore.getFieldSetMap(), rs.getMetaData());
        while (rs.next()) {
            list.add(buildInstance(sqlStore.getConstructor(), fields, rs));
        }
        return list;
    }

    private static <T> T buildInstance(Constructor<T> constructor, FieldSetHandler[] fields, ResultSet rs) throws Exception {
        T target = constructor.newInstance();
        int i = 1;
        for (FieldSetHandler field : fields) {
            if (field == null) continue;
            field.apply(target, rs, i++);
        }
        return target;
    }

    private static FieldSetHandler[] toFields(Map<String, FieldSetHandler> fieldMap, ResultSetMetaData metaData) throws Exception {
        int l = metaData.getColumnCount();
        FieldSetHandler[] fields = new FieldSetHandler[l];
        for (int i = 0; i < l; i++) {
            fields[i] = fieldMap.get(metaData.getColumnLabel(i + 1));
        }
        return fields;
    }

}

package pers.clare.core.sqlquery;

import pers.clare.core.sqlquery.exception.SQLQueryException;
import pers.clare.core.sqlquery.function.FieldSetHandler;
import pers.clare.core.sqlquery.page.Pagination;

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
        return buildPaginationSQL(pagination, new StringBuilder(sql));
    }

    public static String buildPaginationSQL(
            Pagination pagination
            , StringBuilder sql
    ) {
        String[] sorts = pagination.getSorts();
        if (sorts != null) {
            sql.append(" order by ");
            for (String sort : sorts) {
                sql.append(sort)
                        .append(',');
            }
            sql.delete(sql.length() - 1, sql.length());
        }
        sql.append(" limit ")
                .append(pagination.getSize() * pagination.getPage())
                .append(',')
                .append(pagination.getSize());
        return sql.toString();
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

    public static <T> T toInstance(SQLStore<T> sqlStore, ResultSet rs) throws Exception {
        FieldSetHandler[] fields = toFields(sqlStore.fieldSetMap, rs.getMetaData());
        if (rs.next()) {
            return buildInstance(sqlStore.constructor, fields, rs);
        }
        return null;
    }

    public static <T> Set<T> toSetInstance(SQLStore<T> sqlStore, ResultSet rs) throws Exception {
        Set<T> result = new HashSet<>();
        FieldSetHandler[] fields = toFields(sqlStore.fieldSetMap, rs.getMetaData());
        while (rs.next()) {
            result.add(buildInstance(sqlStore.constructor, fields, rs));
        }
        return result;
    }

    public static <T> List<T> toInstances(SQLStore<T> sqlStore, ResultSet rs) throws Exception {
        List<T> list = new ArrayList<>();
        FieldSetHandler[] fields = toFields(sqlStore.fieldSetMap, rs.getMetaData());
        while (rs.next()) {
            list.add(buildInstance(sqlStore.constructor, fields, rs));
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
            fields[i] = fieldMap.get(metaData.getColumnName(i + 1));
        }
        return fields;
    }
}

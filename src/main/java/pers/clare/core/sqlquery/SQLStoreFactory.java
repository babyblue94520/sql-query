package pers.clare.core.sqlquery;

import pers.clare.core.sqlquery.exception.SQLQueryException;
import pers.clare.core.sqlquery.function.FieldGetHandler;
import pers.clare.core.sqlquery.function.FieldSetHandler;
import pers.clare.core.sqlquery.naming.NamingStrategy;
import pers.clare.util.Asserts;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.lang.reflect.*;
import java.util.*;
import java.util.regex.Pattern;

public class SQLStoreFactory {
    private static final Pattern defaultValuePattern = Pattern.compile("^.+default\\s+[']?([^\\s']+)[\\s']?$", Pattern.CASE_INSENSITIVE);

    static Map<Class<?>, SQLStore<?>> sqlStoreCacheMap = new HashMap<>();

    SQLStoreFactory() {
    }

    public static <T> SQLStore<T> find(Class<T> clazz) {
        SQLStore<T> store = (SQLStore<T>) sqlStoreCacheMap.get(clazz);
        Asserts.notNull(store, "%s have not build SQLStore", clazz.getName());
        return store;
    }

    public static boolean isIgnore(Class<?> clazz) {
        return clazz == null
                || clazz.isPrimitive()
                || clazz.getName().startsWith("java.lang")
                || clazz.isArray()
                || Collection.class.isAssignableFrom(clazz)
                || clazz.isEnum()
                || clazz.isInterface()
                ;
    }

    public static <T> SQLStore<T> build(Class<T> clazz, boolean crud) {
        if (isIgnore(clazz)) throw new Error(String.format("%s can not build SQLStore", clazz));
        SQLStore<T> store = (SQLStore<T>) sqlStoreCacheMap.get(clazz);
        if (store != null
                && (!crud || store.crud)) return store;
        if (crud) {
            String tableName = NamingStrategy.convert(clazz.getSimpleName());
            StringBuilder selectColumns = new StringBuilder();
            StringBuilder insertColumns = new StringBuilder();
            StringBuilder insertAutoKeyColumns = new StringBuilder();
            StringBuilder whereId = new StringBuilder(" where ");
            StringBuilder values = new StringBuilder("values(");
            StringBuilder autoKeyValues = new StringBuilder("values(");
            StringBuilder updateSet = new StringBuilder();
            Field[] fields = clazz.getDeclaredFields();
            int length = fields.length;
            int keyCount = 0, insertCount = 0, updateCount = 0;
            Map<String, FieldSetHandler> fieldSetMap = new HashMap<>(fields.length * 3);
            Map<String, FieldGetHandler> fieldGetMap = new HashMap<>(fields.length);
            Field[] keyMethods = new Field[length];
            Field[] insertMethods = new Field[length];
            Field[] updateMethods = new Field[length];
            Column column;
            Id id;
            String columnName, fieldName;
            String name;
            Field autoKey = null;
            FieldSetHandler fieldSetHandler;
            FieldGetHandler fieldGetHandler;
            for (Field field : fields) {
                field.setAccessible(true);
                column = field.getAnnotation(Column.class);
                fieldName = field.getName();
                columnName = column == null ? NamingStrategy.convert(field.getName()) : column.name();
                name = columnName.replaceAll("`", "");
                if (field.getType() == Object.class) {
                    fieldSetHandler = (target, rs, index) -> field.set(target, rs.getObject(index));
                } else {
                    fieldSetHandler = (target, rs, index) -> field.set(target, rs.getObject(index, field.getType()));
                }

                if (column.nullable()) {
                    fieldGetHandler = (target) -> field.get(target);
                } else {

                    fieldGetHandler = (target) -> {
                        Object value = field.get(target);
                        if (value == null) {
                            return value;
                        } else {
                            return value;
                        }
                    };
                }

                fieldSetMap.put(fieldName, fieldSetHandler);
                fieldSetMap.put(name, fieldSetHandler);
                fieldSetMap.put(name.toUpperCase(), fieldSetHandler);
                if (field.getAnnotation(Transient.class) != null) continue;

                id = field.getAnnotation(Id.class);
                if (id == null) {
                    if (column == null) {
                        insertMethods[insertCount++] = field;
                        insertColumns.append(columnName).append(',');
                        insertAutoKeyColumns.append(columnName).append(',');
                        appendValue(values, fieldName);
                        appendValue(autoKeyValues, fieldName);

                        updateMethods[updateCount++] = field;
                        appendSet(updateSet, columnName, fieldName);
                    } else {
                        if (column.insertable()) {
                            insertMethods[insertCount++] = field;
                            insertColumns.append(columnName).append(',');
                            insertAutoKeyColumns.append(columnName).append(',');
                            appendValue(values, fieldName);
                            appendValue(autoKeyValues, fieldName);
                        }
                        if (column.updatable()) {
                            updateMethods[updateCount++] = field;
                            appendSet(updateSet, columnName, fieldName);
                        }
                    }
                } else {
                    if (field.getAnnotation(GeneratedValue.class) == null) {
                        insertMethods[insertCount++] = field;
                        insertColumns.append(columnName).append(',');
                        appendValue(values, fieldName);
                    } else {
                        autoKey = field;
                        autoKey.setAccessible(true);
                    }
                    appendValue(autoKeyValues, fieldName);
                    insertAutoKeyColumns.append(columnName).append(',');
                    keyMethods[keyCount++] = field;
                    whereId.append(columnName)
                            .append('=')
                            .append(':')
                            .append(fieldName)
                            .append(" and ");
                }
                selectColumns.append(columnName).append(',');

            }
            Field[] temp = insertMethods;
            insertMethods = new Field[insertCount];
            System.arraycopy(temp, 0, insertMethods, 0, insertCount);
            temp = updateMethods;
            updateMethods = new Field[updateCount];
            System.arraycopy(temp, 0, updateMethods, 0, updateCount);
            temp = keyMethods;
            keyMethods = new Field[keyCount];
            System.arraycopy(temp, 0, keyMethods, 0, keyCount);

            selectColumns.delete(selectColumns.length() - 1, selectColumns.length());
            insertColumns.delete(insertColumns.length() - 1, insertColumns.length());
            insertAutoKeyColumns.delete(insertAutoKeyColumns.length() - 1, insertAutoKeyColumns.length());
            values.replace(values.length() - 1, values.length(), ")");
            autoKeyValues.replace(autoKeyValues.length() - 1, autoKeyValues.length(), ")");
            updateSet.delete(updateSet.length() - 1, updateSet.length());
            whereId.delete(whereId.length() - 5, whereId.length());

            try {
                store = new SQLStore(clazz.getConstructor(), crud, fieldSetMap, fieldGetMap, autoKey, keyMethods, insertMethods, updateMethods
                        , buildCount(tableName)
                        , buildCountById(tableName, whereId)
                        , buildSelect(tableName, selectColumns)
                        , buildSelectById(tableName, selectColumns, whereId)
                        , buildInsertAutoKey(tableName, insertAutoKeyColumns, autoKeyValues)
                        , buildInsert(tableName, insertColumns, values)
                        , buildUpdate(tableName, updateSet, whereId)
                        , buildDeleteAll(tableName)
                        , buildDeleteById(tableName, whereId)
                );
            } catch (NoSuchMethodException e) {
                throw new SQLQueryException(e.getMessage());
            }
        } else {
            Field[] fields = clazz.getDeclaredFields();
            Map<String, FieldSetHandler> fieldSetMap = new HashMap<>(fields.length * 3);
            Map<String, FieldSetHandler> fieldGetMap = new HashMap<>(fields.length);
            Column column;
            String columnName, fieldName;
            String name;
            FieldSetHandler fieldSetHandler;
            for (Field field : fields) {
                field.setAccessible(true);
                column = field.getAnnotation(Column.class);
                fieldName = field.getName();
                columnName = column == null ? NamingStrategy.convert(field.getName()) : column.name();
                name = columnName.replaceAll("`", "");
                if (field.getType() == Object.class) {
                    fieldSetHandler = (target, rs, index) -> field.set(target, rs.getObject(index));
                } else {
                    fieldSetHandler = (target, rs, index) -> field.set(target, rs.getObject(index, field.getType()));
                }
                fieldSetMap.put(fieldName, fieldSetHandler);
                fieldSetMap.put(name, fieldSetHandler);
                fieldSetMap.put(name.toUpperCase(), fieldSetHandler);
                if (field.getAnnotation(Transient.class) != null) continue;
            }
            try {
                store = new SQLStore(clazz.getConstructor(), fieldSetMap, fieldGetMap);
            } catch (NoSuchMethodException e) {
                throw new SQLQueryException(e.getMessage());
            }
        }
        sqlStoreCacheMap.put(clazz, store);
        return store;
    }

    private static String buildCount(String tableName) {
        int tl = tableName.length();
        char[] chars = new char[21 + tl];
        int index = 0;
        "select count(*) from ".getChars(0, 21, chars, index);
        index += 21;
        tableName.getChars(0, tl, chars, index);
        return new String(chars);
    }

    private static SQLQueryBuilder buildCountById(String tableName, StringBuilder whereId) {
        int tl = tableName.length();
        int wl = whereId.length();
        char[] chars = new char[21 + tl + wl];
        int index = 0;
        "select count(*) from ".getChars(0, 21, chars, index);
        index += 21;
        tableName.getChars(0, tl, chars, index);
        index += tl;
        whereId.getChars(0, wl, chars, index);
        return new SQLQueryBuilder(chars);
    }

    private static String buildSelect(String tableName, StringBuilder selectColumns) {
        int tl = tableName.length();
        int scl = selectColumns.length();

        char[] chars = new char[13 + tl + scl];
        int index = 0;
        "select ".getChars(0, 7, chars, index);
        index += 7;
        selectColumns.getChars(0, scl, chars, index);
        index += scl;
        " from ".getChars(0, 6, chars, index);
        index += 6;
        tableName.getChars(0, tl, chars, index);
        return new String(chars);
    }

    private static SQLQueryBuilder buildSelectById(String tableName, StringBuilder selectColumns, StringBuilder whereId) {
        int tl = tableName.length();
        int scl = selectColumns.length();
        int wl = whereId.length();

        char[] chars = new char[13 + tl + scl + wl];
        int index = 0;
        "select ".getChars(0, 7, chars, index);
        index += 7;
        selectColumns.getChars(0, scl, chars, index);
        index += scl;
        " from ".getChars(0, 6, chars, index);
        index += 6;
        tableName.getChars(0, tl, chars, index);
        index += tl;
        whereId.getChars(0, wl, chars, index);
        return new SQLQueryBuilder(chars);
    }

    private static SQLQueryBuilder buildInsert(String tableName, StringBuilder insertColumns, StringBuilder values) {
        int tl = tableName.length();
        int icl = insertColumns.length();
        int vl = values.length();
        char[] chars = new char[14 + tl + icl + vl];
        int index = 0;
        "insert into ".getChars(0, 12, chars, index);
        index += 12;
        tableName.getChars(0, tl, chars, index);
        index += tl;
        chars[index++] = '(';
        insertColumns.getChars(0, icl, chars, index);
        index += icl;
        chars[index++] = ')';
        values.getChars(0, vl, chars, index);
        return new SQLQueryBuilder(chars);
    }

    private static SQLQueryBuilder buildInsertAutoKey(String tableName, StringBuilder insertAutoKeyColumns, StringBuilder autoKeyValues) {
        int tl = tableName.length();
        int iakcl = insertAutoKeyColumns.length();
        int akvl = autoKeyValues.length();
        char[] chars = new char[14 + tl + iakcl + akvl];
        int index = 0;
        "insert into ".getChars(0, 12, chars, index);
        index += 12;
        tableName.getChars(0, tl, chars, index);
        index += tl;
        chars[index++] = '(';
        insertAutoKeyColumns.getChars(0, iakcl, chars, index);
        index += iakcl;
        chars[index++] = ')';
        autoKeyValues.getChars(0, akvl, chars, index);
        return new SQLQueryBuilder(chars);
    }

    private static SQLQueryBuilder buildUpdate(String tableName, StringBuilder updateSet, StringBuilder whereId) {
        int tl = tableName.length();
        int ul = updateSet.length();
        int wl = whereId.length();

        char[] chars = new char[12 + tl + ul + wl];
        int index = 0;
        "update ".getChars(0, 7, chars, index);
        index += 7;
        tableName.getChars(0, tl, chars, index);
        index += tl;
        " set ".getChars(0, 5, chars, index);
        index += 5;
        updateSet.getChars(0, ul, chars, index);
        index += ul;
        whereId.getChars(0, wl, chars, index);
        return new SQLQueryBuilder(chars);
    }

    private static String buildDeleteAll(String tableName) {
        int tl = tableName.length();
        char[] chars = new char[12 + tl];
        int index = 0;
        "delete from ".getChars(0, 12, chars, index);
        index += 12;
        tableName.getChars(0, tl, chars, index);
        return new String(chars);
    }

    private static SQLQueryBuilder buildDeleteById(String tableName, StringBuilder whereId) {
        int tl = tableName.length();
        int wl = whereId.length();
        char[] chars = new char[12 + tl + wl];
        int index = 0;
        "delete from ".getChars(0, 12, chars, index);
        index += 12;
        tableName.getChars(0, tl, chars, index);
        index += tl;
        whereId.getChars(0, wl, chars, index);
        return new SQLQueryBuilder(chars);
    }

    private static void appendValue(StringBuilder sb, String name) {
        sb.append(':')
                .append(name)
                .append(',');
    }

    private static void appendSet(StringBuilder sb, String column, String name) {
        sb.append(column)
                .append('=')
                .append(':')
                .append(name)
                .append(',');
    }

    private static Object getDefaultValue(Field field, String columnDefinition) {
        if (columnDefinition.length() == 0) {
            return null;
        }
        String value = defaultValuePattern.matcher(columnDefinition).replaceAll("$1");
        Class<?> clazz = field.getType();
        if (Object.class == clazz || String.class == clazz) {
            return value;
        }
        if (Integer.class == clazz) {
            return Integer.valueOf(value);
        }
        if (Integer.class == clazz) {
            return Integer.valueOf(value);
        }
        return value;
    }

    public static void main(String[] args) {
        String columnDefinition = "vardad default aaaa";
        System.out.println(defaultValuePattern.matcher(columnDefinition).replaceAll("$1"));
    }
}

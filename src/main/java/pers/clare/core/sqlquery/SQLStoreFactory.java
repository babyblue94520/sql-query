package pers.clare.core.sqlquery;

import pers.clare.core.util.Asserts;
import pers.clare.demo.data.entity.User;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.lang.reflect.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

public interface SQLStoreFactory {

    Map<Class, SQLStore> entityMap = new HashMap<>();

    static <T> SQLStore<T> find(Class<T> clazz) {
        SQLStore store = entityMap.get(clazz);
        Asserts.notNull(store, "%s have not build SQLStore", clazz.getName());
        return store;
    }

    static boolean isIgnore(Class<?> clazz) {
        return clazz == null
                || clazz.isPrimitive()
                || clazz.getName().startsWith("java.lang")
                || clazz.isArray()
                || Collection.class.isAssignableFrom(clazz)
                || clazz.isEnum()
                || clazz.isInterface()
                ;
    }

    static <T> SQLStore<T> build(Class<T> clazz, boolean crud) {
        if (isIgnore(clazz)) throw new Error(String.format("%s can not build SQLStore", clazz));
        SQLStore store = entityMap.get(clazz);
        if (store != null
                && ((crud && store.crud) || !crud)) return store;

        Map<Integer, Constructor<T>> constructorMap = new HashMap<>();
        Constructor<T>[] constructors = (Constructor<T>[]) clazz.getConstructors();
        for (Constructor<T> constructor : constructors) {
            if (constructor.getParameterCount() > 0) {
                constructorMap.put(constructor.getParameterCount(), constructor);
            }
        }
        if (crud) {
            String tableName = SQLUtil.convert(clazz.getSimpleName());
            StringBuilder selectColumns = new StringBuilder();
            StringBuilder insertColumns = new StringBuilder();
            StringBuilder whereId = new StringBuilder(" where ");
            StringBuilder values = new StringBuilder("values(");
            StringBuilder updateSet = new StringBuilder();
            Field[] fields = clazz.getDeclaredFields();
            int length = fields.length;
            int keyCount = 0, insertCount = 0, updateCount = 0;
            Field[] keyMethods = new Field[length];
            Field[] insertMethods = new Field[length];
            Field[] updateMethods = new Field[length];
            Column column;
            Id id;
            String name, getterName;

            Map<String, Method> methodMap = new HashMap<>();
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                if (method.getName().startsWith("get")) {
                    methodMap.put(method.getName(), method);
                }
            }
            Field autoKey = null;
            for (Field field : fields) {
                if (field.getAnnotation(Transient.class) != null) continue;
                field.setAccessible(true);
                column = field.getAnnotation(Column.class);
                name = column == null ? SQLUtil.convert(field.getName()) : column.name();
                id = field.getAnnotation(Id.class);
                if (id == null) {
                    if (column == null) {
                        insertMethods[insertCount++] = field;
                        insertColumns.append(name).append(',');
                        appendValue(values, name);
                        updateMethods[updateCount++] = field;
                        appendSet(updateSet, name);
                    } else {
                        if (column.insertable()) {
                            insertMethods[insertCount++] = field;
                            insertColumns.append(name).append(',');
                            appendValue(values, name);
                        }
                        if (column.updatable()) {
                            updateMethods[updateCount++] = field;
                            appendSet(updateSet, name);
                        }
                    }
                } else {
                    if (field.getAnnotation(GeneratedValue.class) == null) {
                        insertMethods[insertCount++] = field;
                        insertColumns.append(name).append(',');
                        appendValue(values, name);
                    } else {
                        autoKey = field;
                        autoKey.setAccessible(true);
                    }
                    keyMethods[keyCount++] = field;
                    whereId.append(name)
                            .append('=')
                            .append(':')
                            .append(field.getName())
                            .append(" and ");
                }
                selectColumns.append(name).append(',');

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
            values.replace(values.length() - 1, values.length(), ")");
            updateSet.delete(updateSet.length() - 1, updateSet.length());
            whereId.delete(whereId.length() - 5, whereId.length());

            int tl = tableName.length(), scl = selectColumns.length(), icl = insertColumns.length(), vl = values.length(), ul = updateSet.length(), wl = whereId.length();
            char[] chars;
            int index;

            // count(*)
            chars = new char[21 + tl];
            index = 0;
            "select count(*) from ".getChars(0, 21, chars, index);
            index += 6;
            tableName.getChars(0, tl, chars, index);
            String count = new String(chars);
            // countById(*)
            chars = new char[21 + tl + wl];
            index = 0;
            "select count(*) from ".getChars(0, 21, chars, index);
            index += 6;
            tableName.getChars(0, tl, chars, index);
            index += tl;
            whereId.getChars(0, wl, chars, index);
            SQLQueryBuilder countById = new SQLQueryBuilder(chars);

            // select all
            chars = new char[13 + tl + scl];
            index = 0;
            "select ".getChars(0, 7, chars, index);
            index += 7;
            selectColumns.getChars(0, scl, chars, index);
            index += scl;
            " from ".getChars(0, 6, chars, index);
            index += 6;
            tableName.getChars(0, tl, chars, index);
            String select = new String(chars);

            // select one
            chars = new char[13 + tl + scl + wl];
            index = 0;
            "select ".getChars(0, 7, chars, index);
            index += 7;
            selectColumns.getChars(0, scl, chars, index);
            index += scl;
            " from ".getChars(0, 6, chars, index);
            index += 6;
            tableName.getChars(0, tl, chars, index);
            index += tl;
            whereId.getChars(0, wl, chars, index);
            SQLQueryBuilder selectById = new SQLQueryBuilder(chars);

            // insert
            chars = new char[14 + tl + icl + vl];
            index = 0;
            "insert into ".getChars(0, 12, chars, index);
            index += 12;
            tableName.getChars(0, tl, chars, index);
            index += tl;
            chars[index++] = '(';
            insertColumns.getChars(0, icl, chars, index);
            index += icl;
            chars[index++] = ')';
            values.getChars(0, vl, chars, index);
            SQLQueryBuilder insert = new SQLQueryBuilder(chars);

            // update
            chars = new char[12 + tl + ul + wl];
            index = 0;
            "update ".getChars(0, 7, chars, index);
            index += 7;
            tableName.getChars(0, tl, chars, index);
            index += tl;
            " set ".getChars(0, 5, chars, index);
            index += 5;
            updateSet.getChars(0, ul, chars, index);
            index += ul;
            whereId.getChars(0, wl, chars, index);
            SQLQueryBuilder update = new SQLQueryBuilder(chars);

            // delete
            chars = new char[12 + tl];
            index = 0;
            "delete from ".getChars(0, 12, chars, index);
            index += 12;
            tableName.getChars(0, tl, chars, index);
            String deleteAll = new String(chars);

            // deleteById
            chars = new char[12 + tl + wl];
            index = 0;
            "delete from ".getChars(0, 12, chars, index);
            index += 12;
            tableName.getChars(0, tl, chars, index);
            index += tl;
            whereId.getChars(0, wl, chars, index);
            SQLQueryBuilder deleteById = new SQLQueryBuilder(chars);

            store = new SQLStore(constructorMap, crud, autoKey, keyMethods, insertMethods, updateMethods, count, countById, select, selectById, insert, update, deleteAll, deleteById);
        } else {
            store = new SQLStore(constructorMap);
        }
        entityMap.put(clazz, store);
        return store;
    }

    private static void appendValue(StringBuilder sb, String name) {
        sb.append(':')
                .append(name)
                .append(',');
    }

    private static void appendSet(StringBuilder sb, String name) {
        sb.append('=')
                .append(':')
                .append(name)
                .append(',');
    }

    public static void main(String[] args) {
        System.out.println(User.class.getName());

    }
}

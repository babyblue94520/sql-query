package pers.clare.core.sqlquery;

import pers.clare.core.util.Asserts;
import sun.reflect.FieldAccessor;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public interface SQLStoreFactory {

    public static final Map<Class, SQLStore> entityMap = new HashMap<>();

    public static <T> SQLStore<T> find(Class<T> clazz) {
        SQLStore entity = entityMap.get(clazz);
        Asserts.notNull(entity, "%s is't build SQLStore", clazz.getName());
        return entity;
    }

    public static <T> SQLStore<T> build(Class<T> clazz, boolean crud) {
        Map<Integer, Constructor<T>> constructorMap = new HashMap<>();
        Constructor<T>[] constructors = (Constructor<T>[]) clazz.getConstructors();
        for (Constructor<T> constructor : constructors) {
            if (constructor.getParameterCount() > 0) {
                constructorMap.put(constructor.getParameterCount(), constructor);
            }
        }
        SQLStore entity;
        if (crud) {
            String tableName = SQLUtil.convert(clazz.getSimpleName());
            StringBuilder selectColumns = new StringBuilder();
            StringBuilder insertColumns = new StringBuilder();
            StringBuilder whereId = new StringBuilder(" where ");
            StringBuilder values = new StringBuilder("values(");
            StringBuilder updateSet = new StringBuilder();
            Field[] fields = clazz.getDeclaredFields();
            int length = fields.length;
            int i = 0, keyCount = 0, insertCount = 0, updateCount = 0;
            Method[] keyMethods = new Method[length];
            Method[] insertMethods = new Method[length];
            Method[] updateMethods = new Method[length];
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
            FieldAccessor autoKey2 = null;
            Method method;
            for (Field field : fields) {
                if (field.getAnnotation(Transient.class) != null) continue;
                getterName = SQLUtil.toGetterName(field.getName());
                method = methodMap.get(getterName);
                if (method == null) continue;
                column = field.getAnnotation(Column.class);
                name = column == null ? SQLUtil.convert(field.getName()) : column.name();
                id = field.getAnnotation(Id.class);
                if (id == null) {
                    if (column == null) {
                        insertMethods[insertCount++] = method;
                        insertColumns.append(name)
                                .append(',');
                        values.append("?,");
                        updateMethods[updateCount++] = method;
                        updateSet.append(name)
                                .append("=?,");
                    } else {
                        if (column.insertable()) {
                            insertMethods[insertCount++] = method;
                            insertColumns.append(name)
                                    .append(',');
                            values.append("?,");
                        }
                        if (column.updatable()) {
                            updateMethods[updateCount++] = method;
                            updateSet.append(name)
                                    .append("=?,");
                        }
                    }
                } else {
                    if (field.getAnnotation(GeneratedValue.class) == null) {
                        insertMethods[insertCount++] = method;
                        insertColumns.append(name)
                                .append(',');
                        values.append("?,");
                    } else {
                        autoKey = field;
                        autoKey2 = (FieldAccessor) getFieldAccessor(clazz, field);
                    }
                    keyMethods[keyCount++] = method;
                    whereId.append(name)
                            .append("=? and ");
                }
                selectColumns.append(name)
                        .append(',');

            }
            Method[] temp = insertMethods;
            insertMethods = new Method[insertCount];
            System.arraycopy(temp, 0, insertMethods, 0, insertCount);
            temp = updateMethods;
            updateMethods = new Method[updateCount];
            System.arraycopy(temp, 0, updateMethods, 0, updateCount);
            temp = keyMethods;
            keyMethods = new Method[keyCount];
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
            chars = new char[21 + tl + wl];
            index = 0;
            "select count(*) from ".getChars(0, 21, chars, index);
            index += 6;
            tableName.getChars(0, tl, chars, index);
            index += tl;
            whereId.getChars(0, wl, chars, index);
            String count = new String(chars);

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
            String select = new String(chars);

            // insert
            chars = new char[9 + tl + icl + vl];
            index = 0;
            "insert into ".getChars(0, 7, chars, index);
            index += 7;
            tableName.getChars(0, tl, chars, index);
            index += tl;
            chars[index++] = '(';
            insertColumns.getChars(0, icl, chars, index);
            index += icl;
            chars[index++] = ')';
            values.getChars(0, vl, chars, index);
            String insert = new String(chars);

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
            String update = new String(chars);

            // delete
            chars = new char[12 + tl  + wl];
            index = 0;
            "delete from ".getChars(0, 12, chars, index);
            index += 12;
            tableName.getChars(0, tl, chars, index);
            index += tl;
            whereId.getChars(0, wl, chars, index);
            String delete = new String(chars);
//            System.out.println(select);
//            System.out.println(insert);
//            System.out.println(update);
//            System.out.println(delete);
            entity = new SQLStore(constructorMap, crud, autoKey, autoKey2, keyMethods, insertMethods, updateMethods, count, select, insert, update, delete);
        } else {
            entity = new SQLStore(constructorMap);
        }
        entityMap.put(clazz, entity);
        return entity;
    }

    static <T> Object getFieldAccessor(Class<T> clazz, Field field) {
        try {
            Method method = Field.class.getDeclaredMethod("getFieldAccessor", Object.class);
            method.setAccessible(true);
            return method.invoke(field, clazz.getConstructor().newInstance());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }
}

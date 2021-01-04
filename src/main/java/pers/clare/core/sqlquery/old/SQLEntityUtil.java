package pers.clare.core.sqlquery.old;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;


public class SQLEntityUtil {

    public static <T> int setValue(PreparedStatement ps, T entity, Method[] methods) throws IllegalAccessException, SQLException, InvocationTargetException {
        return setValue(ps, entity, methods, 1);
    }

    public static <T> int setValue(PreparedStatement ps, T entity, Method[] methods, int index) throws IllegalAccessException, SQLException, InvocationTargetException {
        for (Method f : methods) {
            ps.setObject(index++, f.invoke(entity));
        }
        return index;
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

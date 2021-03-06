package pers.clare.core.sqlquery.method;

import org.aopalliance.intercept.MethodInterceptor;
import pers.clare.core.sqlquery.page.Sort;
import pers.clare.core.sqlquery.util.MethodUtil;
import pers.clare.core.sqlquery.SQLInjector;
import pers.clare.core.sqlquery.SQLStoreFactory;
import pers.clare.core.sqlquery.SQLStoreService;
import pers.clare.core.sqlquery.annotation.Sql;
import pers.clare.core.sqlquery.exception.SQLQueryException;
import pers.clare.core.sqlquery.page.Next;
import pers.clare.core.sqlquery.page.Page;
import pers.clare.core.sqlquery.page.Pagination;

import java.lang.reflect.Method;
import java.util.*;


public class SQLMethodFactory {

    private SQLMethodFactory() {
    }

    public static Map<Method, MethodInterceptor> create(
            Class<?> repositoryInterface
            , SQLStoreService sqlStoreService

    ) {
        Method[] methods = repositoryInterface.getDeclaredMethods();
        Map<String, String> contents = SQLInjector.getContents(repositoryInterface);
        Map<Method, MethodInterceptor> methodInterceptors = new HashMap<>();
        String command;
        SQLMethod sqlMethod;
        for (Method method : methods) {
            command = findSqlCommand(contents, method);
            if (command == null) {
                throw new SQLQueryException(String.format("%s.%s method must set XML or Sql.query", repositoryInterface.getName(), method.getName()));
            }
            sqlMethod = buildMethod(method, command);
            if (sqlMethod == null) {
                throw new SQLQueryException(String.format("%s not support return type", method.getName()));
            }
            sqlMethod.setMethod(method);
            sqlMethod.setSql(command);
            sqlMethod.setSqlStoreService(sqlStoreService);
            methodInterceptors.put(method, sqlMethod);
        }
        return methodInterceptors;
    }

    /**
     * build method interceptor by command type
     */
    private static SQLMethod buildMethod(Method method, String command) {
        if (command.startsWith("select")) {
            Class<?> returnType = method.getReturnType();
            if (Collection.class.isAssignableFrom(returnType)) {
                if (returnType == Set.class) {
                    return buildSet(method);
                } else {
                    return buildList(method);
                }
            } else if (returnType == Map.class) {
                return new BasicTypeMap(getMapValueClass(method));
            } else if (Page.class.isAssignableFrom(returnType)) {
                return buildPage(method);
            } else if (Next.class.isAssignableFrom(returnType)) {
                // TODO
                return null;
            } else {
                if (SQLStoreFactory.isIgnore(returnType)) {
                    return new BasicType(returnType);
                } else {
                    return new SQLEntity(returnType);
                }
            }
        } else {
            return new SQLUpdateMethod();
        }
    }

    /**
     * build set result method interceptor
     */
    private static SQLMethod buildSet(Method method) {
        Class<?> valueType = MethodUtil.getReturnClass(method, 0);
        if (valueType == null || valueType == Map.class) {
            return new BasicTypeMapSet(getParameterMapValueClass(method, valueType));
        } else {
            if (SQLStoreFactory.isIgnore(valueType)) {
                return new BasicTypeSet(valueType);
            } else {
                return new SQLEntitySet(valueType);
            }
        }
    }

    /**
     * build list result method interceptor
     */
    private static SQLMethod buildList(Method method) {
        Class<?> valueType = MethodUtil.getReturnClass(method, 0);
        if (valueType == null || valueType == Map.class) {
            return new BasicTypeMapList(getParameterMapValueClass(method, valueType));
        } else {
            if (SQLStoreFactory.isIgnore(valueType)) {
                return new BasicTypeList(valueType);
            } else {
                return new SQLEntityList(valueType);
            }
        }
    }

    /**
     * build page result method interceptor
     */
    private static SQLMethod buildPage(Method method) {
        Class<?> valueType = MethodUtil.getReturnClass(method, 0);
        if (valueType == null || valueType == Map.class) {
            valueType = getParameterMapValueClass(method, null);
            return new BasicTypeMapPage(valueType);
        } else {
            if (SQLStoreFactory.isIgnore(valueType)) {
                return new BasicTypPage(valueType);
            } else {
                return new SQLEntityPage(valueType);
            }
        }
    }

    private static int sortIndexOf(Class<?>[] parameterTypes) {
        for (int i = 0; i < parameterTypes.length; i++)
            if (parameterTypes[i] == Sort.class) return i;
        return -1;
    }

    private static int paginationIndexOf(Class<?>[] parameterTypes) {
        for (int i = 0; i < parameterTypes.length; i++)
            if (parameterTypes[i] == Pagination.class) return i;
        return -1;
    }

    /**
     * get sql string
     */
    private static String findSqlCommand(
            Map<String, String> contents
            , Method method
    ) {
        String command = contents.get(method.getName());
        if (command == null) {
            Sql sql = method.getAnnotation(Sql.class);
            if (sql != null) {
                command = contents.get(sql.name());
                if (command == null) command = sql.value();
            }
        }
        return command;
    }

    private static Class<?> getMapValueClass(Method method) {
        Class<?> valueType = MethodUtil.getReturnClass(method, 0);
        return valueType == null ? Object.class : valueType;
    }

    private static Class<?> getParameterMapValueClass(Method method, Class<?> valueType) {
        if (valueType == null) return Object.class;
        valueType = (Class<?>) MethodUtil.getType(MethodUtil.getReturnType(method, 0), 1);
        return valueType == null ? Object.class : valueType;
    }
}

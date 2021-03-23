package pers.clare.core.sqlquery.method;

import org.aopalliance.intercept.MethodInterceptor;
import pers.clare.core.sqlquery.SQLInjector;
import pers.clare.core.sqlquery.SQLStoreFactory;
import pers.clare.core.sqlquery.SQLStoreService;
import pers.clare.core.sqlquery.annotation.Sql;
import pers.clare.core.sqlquery.exception.SQLQueryException;
import pers.clare.core.sqlquery.method.*;
import pers.clare.core.sqlquery.page.Next;
import pers.clare.core.sqlquery.page.Page;
import pers.clare.core.sqlquery.page.Pagination;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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
        Map<Method, MethodInterceptor> queryMethods = new HashMap<>();
        String command;
        int paginationIndex = -1;
        Class<?> returnType;
        Class<?> valueType;
        for (Method method : methods) {
            command = getCommand(contents, method);
            if (command.startsWith("select")) {
                Class<?>[] parameterTypes = method.getParameterTypes();
                for (int i = 0; i < parameterTypes.length; i++) {
                    if (parameterTypes[i] == Pagination.class) {
                        paginationIndex = i;
                        break;
                    }
                }
                returnType = method.getReturnType();
                if (Collection.class.isAssignableFrom(returnType)) {
                    valueType = getReturnActualClass(method, 0);
                    if (returnType == Set.class) {
                        if (SQLStoreFactory.isIgnore(valueType)) {
                            queryMethods.put(method, new SetResultSetMethod(method, command, sqlStoreService, valueType, paginationIndex));
                        } else {
                            queryMethods.put(method, new SQLStoreSetResultSetMethod(method, command, sqlStoreService, valueType, paginationIndex));
                        }
                    } else {
                        if (SQLStoreFactory.isIgnore(valueType)) {
                            if (valueType == Map.class) {
                                // TODO
                                valueType = (Class<?>) getActualType((ParameterizedType) getReturnActualType(method,0),1);
                                queryMethods.put(method, new MapListResultSetMethod(method, command, sqlStoreService, valueType, paginationIndex));
                            } else {
                                queryMethods.put(method, new ListResultSetMethod(method, command, sqlStoreService, valueType, paginationIndex));
                            }
                        } else {
                            queryMethods.put(method, new SQLStoreListResultSetMethod(method, command, sqlStoreService, valueType, paginationIndex));
                        }
                    }
                } else if (returnType == Map.class) {
                    queryMethods.put(method, new MapResultSetMethod(method, command, sqlStoreService, getReturnActualClass(method, 1)));
                } else if (Page.class.isAssignableFrom(returnType)) {
                    valueType = getReturnActualClass(method, 0);
                    if (SQLStoreFactory.isIgnore(valueType)) {
                        if (valueType == Map.class) {
                            valueType = (Class<?>) getActualType((ParameterizedType) getReturnActualType(method,0),1);
                            queryMethods.put(method, new MapPageMethod(method, command, sqlStoreService, valueType, paginationIndex));
                        } else {
                            // TODO
                            throw new SQLQueryException("");
                        }
                    } else {
                        queryMethods.put(method, new SQLStorePageMethod(method, command, sqlStoreService, valueType, paginationIndex));
                    }
                } else if (Next.class.isAssignableFrom(returnType)) {
                    valueType = getReturnActualClass(method, 0);
                    // TODO
                } else {
                    if (SQLStoreFactory.isIgnore(returnType)) {
                        queryMethods.put(method, new OneResultSetMethod(method, command, sqlStoreService, returnType, paginationIndex));
                    } else {
                        queryMethods.put(method, new SQLStoreResultSetMethod(method, command, sqlStoreService, returnType, paginationIndex));
                    }
                }
            } else {
                queryMethods.put(method, new SQLUpdateMethod(method, command, sqlStoreService));
            }
        }
        return queryMethods;
    }

    private static String getCommand(
            Map<String, String> contents
            , Method method
    ) {
        String command = contents.get(method.getName());
        if (command == null) {
            Sql sql = method.getAnnotation(Sql.class);
            if (sql != null) {
                command = contents.get(sql.name());
                if (command == null) command = sql.query();
            }
        }
        if (command == null)
            throw new SQLQueryException(String.format("%s method must set XML or Sql.query", method.getName()));
        return command;
    }

    private static Class<?> getReturnActualClass(Method method, int index) {
        return (Class<?>) getReturnActualType(method, index);
    }

    private static Type getReturnActualType(Method method, int index) {
        return getActualType((ParameterizedType) method.getGenericReturnType(), index);
    }

    private static Type getActualType(ParameterizedType parameterizedType, int index) {
        return parameterizedType.getActualTypeArguments()[index];
    }
}

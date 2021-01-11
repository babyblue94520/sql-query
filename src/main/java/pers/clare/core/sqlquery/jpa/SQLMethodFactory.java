package pers.clare.core.sqlquery.jpa;

import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.data.domain.Pageable;
import pers.clare.core.sqlquery.*;
import pers.clare.core.sqlquery.annotation.Sql;
import pers.clare.core.sqlquery.exception.SQLQueryException;
import pers.clare.core.sqlquery.handler.*;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
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
        boolean page;
        Class<?> returnType;
        Class<?> valueType;
        for (Method method : methods) {
            command = getCommand(contents, method);
            if (command.startsWith("select")) {
                for (Class<?> parameterClazz : method.getParameterTypes()) {
                    if (parameterClazz == Pageable.class) {
                        page = true;
                        break;
                    }
                }
                returnType = method.getReturnType();
                if (Collection.class.isAssignableFrom(returnType)) {
                    valueType = getReturnActualType(method, 0);
                    if (returnType == Set.class) {
                        if (SQLStoreFactory.isIgnore(valueType)) {
                            queryMethods.put(method, new SetResultSetHandler(method, command, sqlStoreService, valueType));
                        } else {
                            queryMethods.put(method, new SQLStoreSetResultSetHandler(method, command, sqlStoreService, valueType));
                        }
                    } else {
                        if (SQLStoreFactory.isIgnore(valueType)) {
                            if (valueType == Map.class) {
                                queryMethods.put(method, new MapListResultSetHandler(method, command, sqlStoreService, valueType));
                            } else {
                                queryMethods.put(method, new ListResultSetHandler(method, command, sqlStoreService, valueType));
                            }
                        } else {
                            queryMethods.put(method, new SQLStoreListResultSetHandler(method, command, sqlStoreService, valueType));

                        }
                    }
                } else if (returnType == Map.class) {
                    queryMethods.put(method, new MapResultSetHandler(method, command, sqlStoreService, getReturnActualType(method, 1)));
                } else {
                    if (SQLStoreFactory.isIgnore(returnType)) {
                        queryMethods.put(method, new OneResultSetHandler(method, command, sqlStoreService, returnType));
                    } else {
                        queryMethods.put(method, new SQLStoreResultSetHandler(method, command, sqlStoreService, returnType));
                    }
                }
            } else {
                queryMethods.put(method, new SQLUpdateHandler(method, command, sqlStoreService));
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
            throw new SQLQueryException(String.format("{} method must set XML or Sql.query", method.getName()));
        return command;
    }

    private static Class<?> getReturnActualType(Method method, int index) {
        return (Class<?>) ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[index];
    }
}

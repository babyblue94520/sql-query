package pers.clare.core.sqlquery.jpa;

import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;
import pers.clare.core.sqlquery.*;
import pers.clare.core.sqlquery.annotation.SQLEntity;
import pers.clare.core.sqlquery.annotation.Sql;
import pers.clare.core.sqlquery.exception.SQLQueryException;
import pers.clare.core.sqlquery.handler.*;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class SQLQueryMethodFactory {

    private SQLQueryMethodFactory() {
    }

    public static Map<Method, MethodInterceptor> create(
            SQLQueryService sqlQueryService
            , SQLStoreService sqlStoreService
            , SQLService sqlService
            , Class<?> repositoryInterface
    ) {
        Method[] methods = repositoryInterface.getDeclaredMethods();
        Map<String, String> contents = SQLInjector.getContents(repositoryInterface);
        Map<Method, MethodInterceptor> queryMethods = new HashMap<>();
        String command;
        Sql sql;
        boolean page;
        Class<?> returnType;
        Class<?> valueClass;
        for (Method method : methods) {
            command = contents.get(method.getName());
            if (command == null) {
                sql = method.getAnnotation(Sql.class);
                if (sql != null) {
                    command = contents.get(sql.name());
                    if (command == null) command = sql.query();
                }
            }
            if (command == null) {
                throw new SQLQueryException(String.format("{} method must set XML or Sql.query", method.getName()));
            }
            if (command.startsWith("select")) {
                for (Class<?> parameterClazz : method.getParameterTypes()) {
                    if (parameterClazz == Pageable.class) {
                        page = true;
                        break;
                    }
                }
                returnType = method.getReturnType();
                SQLEntity sqlEntity = returnType.getAnnotation(SQLEntity.class);
                if (Collection.class.isAssignableFrom(returnType)) {
                    valueClass = getReturnActualType(method,0);
                    if (returnType == Set.class) {

                    } else {

                    }
                    queryMethods.put(method, new OneListResultSetHandler(command, sqlService, returnType));
                } else if (returnType == Map.class) {
                    valueClass = getReturnActualType(method,1);
                    if(valueClass==Object.class){
                        queryMethods.put(method, new ObjectMapResultSetHandler(command, sqlService));
                    }else{
                        queryMethods.put(method, new MapResultSetHandler(command, sqlService,valueClass));
                    }
                } else {
                    if (sqlEntity == null) {
                        queryMethods.put(method, new OneResultSetHandler(command, sqlService, returnType));
                    } else {
                        queryMethods.put(method, new SQLStoreResultSetHandler(command, sqlStoreService, SQLStoreFactory.build(returnType, false)));
                    }
                }
            } else {
                char[] cs = command.toCharArray();
                if (SQLQueryReplaceBuilder.findKeyCount(cs) > 0) {
                    queryMethods.put(method, new SQLQueryReplaceUpdateHandler(new SQLQueryReplaceBuilder(cs), sqlQueryService, method.getParameters()));
                } else if (SQLQueryBuilder.findKeyCount(cs) > 0) {
                    queryMethods.put(method, new SQLQueryUpdateHandler(new SQLQueryBuilder(cs), sqlQueryService, method.getParameters()));
                } else {
                    queryMethods.put(method, new SQLUpdateHandler(command, sqlService));
                }
            }
        }
        return queryMethods;
    }

    private static Class<?> getReturnActualType(Method method,int index){
       return (Class<?>) ((ParameterizedType)method.getGenericReturnType()).getActualTypeArguments()[index];
    }
}

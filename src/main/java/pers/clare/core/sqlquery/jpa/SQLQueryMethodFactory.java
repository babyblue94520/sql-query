package pers.clare.core.sqlquery.jpa;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


public abstract class SQLQueryMethodFactory {

    SQLQueryMethodFactory(){}

    public static Map<Method,Object> create(Class<?> repositoryInterface){
        Method[] methods = repositoryInterface.getDeclaredMethods();
        Map<Method,Object> queryMethods = new HashMap<>();
        for (Method method : methods) {
            queryMethods.put(method,new Object());
        }
        return queryMethods;
    }
}

package pers.clare.core.sqlquery.jpa;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class SQLQueryMethodInterceptor implements MethodInterceptor {
    private final Map<Method,Object> queryMethods;

    public SQLQueryMethodInterceptor(Map<Method,Object> queryMethods) {
        this.queryMethods = queryMethods;
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        Object o = queryMethods.get(methodInvocation.getMethod());
        return null;
    }
}

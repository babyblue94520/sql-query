package pers.clare.core.sqlquery.jpa;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import pers.clare.core.sqlquery.exception.SQLQueryException;

import java.lang.reflect.Method;
import java.util.Map;

public class SQLQueryMethodInterceptor implements MethodInterceptor {
    private final Map<Method, MethodInterceptor> queryMethods;

    public SQLQueryMethodInterceptor(Map<Method, MethodInterceptor> queryMethods) {
        this.queryMethods = queryMethods;
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        MethodInterceptor handler = queryMethods.get(methodInvocation.getMethod());
        if (handler == null) throw new SQLQueryException("");
        return handler.invoke(methodInvocation);
    }
}

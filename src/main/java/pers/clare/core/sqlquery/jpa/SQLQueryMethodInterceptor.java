package pers.clare.core.sqlquery.jpa;

import lombok.extern.log4j.Log4j2;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import pers.clare.core.sqlquery.exception.SQLQueryException;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Log4j2
public class SQLQueryMethodInterceptor implements MethodInterceptor {
    private Object target;
    private Map<Method, Method> methods;
    private final Map<Method, MethodInterceptor> queryMethods;

    public SQLQueryMethodInterceptor(
            Class<?> interfaceClass
            , Object target
            ,Map<Method, MethodInterceptor> queryMethods
    ) {
        this.target = target;
        this.queryMethods = queryMethods;
        this.methods = new HashMap<>();
        Class<?> targetClass = target.getClass();
        Method targetMethod;
        for (Method method : interfaceClass.getMethods()) {
            try {
                targetMethod = targetClass.getMethod(method.getName(), method.getParameterTypes());
                if (targetMethod != null) {
                    this.methods.put(method, targetMethod);
                }
            } catch (NoSuchMethodException e) {
                log.error(e.getMessage());
            }
        }
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        Method method = this.methods.get(methodInvocation.getMethod());
        if (method == null){
            MethodInterceptor handler = queryMethods.get(methodInvocation.getMethod());
            if (handler == null)
                throw new SQLQueryException(String.format("%s not found", methodInvocation.getMethod()));
            return handler.invoke(methodInvocation);
        }else{
            return method.invoke(target, methodInvocation.getArguments());
        }
    }
}

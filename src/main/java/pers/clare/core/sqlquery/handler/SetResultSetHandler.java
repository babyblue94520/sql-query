package pers.clare.core.sqlquery.handler;

import lombok.AllArgsConstructor;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import pers.clare.core.sqlquery.SQLService;

@AllArgsConstructor
public class SetResultSetHandler implements MethodInterceptor {
    private String sql;
    private SQLService sqlService;
    private Class<?> returnType;

    @Override
    public Object invoke(MethodInvocation methodInvocation) {
        return sqlService.findAll(returnType, sql, methodInvocation.getArguments());
    }
}

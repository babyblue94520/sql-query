package pers.clare.core.sqlquery.handler;

import lombok.AllArgsConstructor;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import pers.clare.core.sqlquery.SQLService;

@AllArgsConstructor
public class SQLUpdateHandler implements MethodInterceptor {
    private String sql;
    private SQLService sqlService;

    @Override
    public Object invoke(MethodInvocation methodInvocation) {
        return sqlService.update(sql, methodInvocation.getArguments());
    }
}

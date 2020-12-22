package pers.clare.core.sqlquery.handler;

import lombok.AllArgsConstructor;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import pers.clare.core.sqlquery.SQLService;

@AllArgsConstructor
public class MapResultSetHandler implements MethodInterceptor {
    private String sql;
    private SQLService sqlService;

    @Override
    public Object invoke(MethodInvocation methodInvocation) {
        return sqlService.find(sql, methodInvocation.getArguments());
    }
}

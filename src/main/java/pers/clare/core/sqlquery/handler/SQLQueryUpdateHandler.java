package pers.clare.core.sqlquery.handler;

import lombok.AllArgsConstructor;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import pers.clare.core.sqlquery.*;

import java.lang.reflect.Parameter;

@AllArgsConstructor
public class SQLQueryUpdateHandler implements MethodInterceptor {
    private SQLQueryBuilder builder;
    private SQLQueryService sqlQueryService;
    private Parameter[] parameters;

    @Override
    public Object invoke(MethodInvocation methodInvocation) {
        Object[] args = methodInvocation.getArguments();
        SQLQuery query = builder.build();
        for (int i = 0, l = parameters.length; i < l; i++) {
            query.value(parameters[i].getName(), args[i]);
        }
        return sqlQueryService.update(query);
    }
}

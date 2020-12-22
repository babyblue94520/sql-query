package pers.clare.core.sqlquery.handler;

import lombok.AllArgsConstructor;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.util.StringUtils;
import pers.clare.core.sqlquery.*;

import java.lang.reflect.Parameter;

@AllArgsConstructor
public class SQLQueryReplaceUpdateHandler implements MethodInterceptor {
    private SQLQueryReplaceBuilder builder;
    private SQLQueryService sqlQueryService;
    private Parameter[] parameters;

    @Override
    public Object invoke(MethodInvocation methodInvocation) {
        SQLQueryReplace replace = builder.build();
        Object[] args = methodInvocation.getArguments();
        Object arg;
        for (int i = 0, l = parameters.length; i < l; i++) {
            arg = args[i];
            if (arg instanceof String) {
                replace.replace(parameters[i].getName(), (String) arg);
            }
        }
        SQLQuery query = replace.buildQuery();
        for (int i = 0, l = parameters.length; i < l; i++) {
            query.value(parameters[i].getName(), args[i]);
        }
        return sqlQueryService.update(query);
    }
}

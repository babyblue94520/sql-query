package pers.clare.core.sqlquery.method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import pers.clare.core.sqlquery.*;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

public abstract class SQLMethod implements MethodInterceptor {
    private static final Object[] emptyArguments = new Object[0];
    protected String sql;
    protected SQLStoreService sqlStoreService;
    protected SQLQueryReplaceBuilder sqlQueryReplaceBuilder;
    protected SQLQueryBuilder sqlQueryBuilder;
    protected Map<String, Integer> replaces;
    protected Map<String, Integer> values;
    protected Method method;
    protected Parameter[] parameters;

    public SQLMethod(Method method, String sql, SQLStoreService sqlStoreService) {
        this.sql = sql;
        this.sqlStoreService = sqlStoreService;
        this.method = method;
        this.parameters = method.getParameters();

        char[] cs = sql.toCharArray();
        if (SQLQueryReplaceBuilder.findKeyCount(cs) > 0) {
            sqlQueryReplaceBuilder = new SQLQueryReplaceBuilder(cs);
            this.replaces = new HashMap<>(sqlQueryReplaceBuilder.getKeySize());
            this.values = new HashMap<>(parameters.length - sqlQueryReplaceBuilder.getKeySize());
            int c = 0;
            for (Parameter p : parameters) {
                if (sqlQueryReplaceBuilder.hasKey(p.getName())) {
                    replaces.put(p.getName(), c++);
                } else {
                    values.put(p.getName(), c++);
                }
            }
        } else if (SQLQueryBuilder.findKeyCount(cs) > 0) {
            sqlQueryBuilder = new SQLQueryBuilder(cs);
        }
    }

    protected String toSql(SQLQueryReplaceBuilder sqlQueryReplaceBuilder, MethodInvocation methodInvocation) {
        SQLQueryReplace replace = sqlQueryReplaceBuilder.build();
        Object[] args = methodInvocation.getArguments();
        for (Map.Entry<String, Integer> entry : replaces.entrySet()) {
            replace.replace(entry.getKey(), String.valueOf(args[entry.getValue()]));
        }
        SQLQuery query = replace.buildQuery();
        for (Map.Entry<String, Integer> entry : values.entrySet()) {
            query.value(entry.getKey(), String.valueOf(args[entry.getValue()]));
        }
        return query.toString();
    }

    protected String toSql(SQLQueryBuilder sqlQueryBuilder, MethodInvocation methodInvocation) {
        Object[] args = methodInvocation.getArguments();
        SQLQuery query = sqlQueryBuilder.build();
        for (int i = 0, l = parameters.length; i < l; i++) {
            query.value(parameters[i].getName(), args[i]);
        }
        return query.toString();
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) {
        if (sqlQueryReplaceBuilder != null) {
            return sqlStoreService.update(toSql(sqlQueryReplaceBuilder, methodInvocation), emptyArguments);
        } else if (sqlQueryBuilder != null) {
            return doInvoke(toSql(sqlQueryBuilder, methodInvocation), emptyArguments);
        } else {
            return doInvoke(sql, methodInvocation.getArguments());
        }
    }

    abstract protected Object doInvoke(String sql, Object[] arguments);
}

package pers.clare.core.sqlquery.method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import pers.clare.core.sqlquery.*;
import pers.clare.core.sqlquery.page.Pagination;
import pers.clare.core.sqlquery.util.SQLUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

public abstract class SQLMethod implements MethodInterceptor {
    protected static final Object[] emptyArguments = new Object[0];
    protected String sql;
    protected SQLStoreService sqlStoreService;
    protected SQLQueryReplaceBuilder sqlQueryReplaceBuilder;
    protected SQLQueryBuilder sqlQueryBuilder;
    protected Map<String, Integer> replaces;
    protected Map<String, Integer> values;
    protected Method method;
    protected Parameter[] parameters;
    protected int paginationIndex = -1;

    public void setSql(String sql) {
        this.sql = sql;
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

    public void setMethod(Method method) {
        this.method = method;
        this.parameters = method.getParameters();
    }

    public void setPaginationIndex(int paginationIndex) {
        this.paginationIndex = paginationIndex;
    }

    public void setSqlStoreService(SQLStoreService sqlStoreService) {
        this.sqlStoreService = sqlStoreService;
    }

    protected String toSql(SQLQueryReplaceBuilder sqlQueryReplaceBuilder, MethodInvocation methodInvocation, Pagination pagination) {
        SQLQueryReplace replace = sqlQueryReplaceBuilder.build();
        Object[] args = methodInvocation.getArguments();
        for (Map.Entry<String, Integer> entry : replaces.entrySet()) {
            replace.replace(entry.getKey(), String.valueOf(args[entry.getValue()]));
        }
        SQLQuery query = replace.buildQuery();
        for (Map.Entry<String, Integer> entry : values.entrySet()) {
            query.value(entry.getKey(), args[entry.getValue()]);
        }
        return query.toString(pagination);
    }

    protected String toSql(SQLQueryBuilder sqlQueryBuilder, MethodInvocation methodInvocation, Pagination pagination) {
        Object[] args = methodInvocation.getArguments();
        SQLQuery query = sqlQueryBuilder.build();
        for (int i = 0, l = parameters.length; i < l; i++) {
            query.value(parameters[i].getName(), args[i]);
        }
        return query.toString(pagination);
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) {
        Pagination pagination = null;
        if (paginationIndex != -1) {
            pagination = (Pagination) methodInvocation.getArguments()[paginationIndex];
        }
        if (sqlQueryReplaceBuilder != null) {
            return doInvoke(toSql(sqlQueryReplaceBuilder, methodInvocation, pagination), emptyArguments);
        } else if (sqlQueryBuilder != null) {
            return doInvoke(toSql(sqlQueryBuilder, methodInvocation, pagination), emptyArguments);
        } else {
            if (pagination == null) {
                return doInvoke(sql, methodInvocation.getArguments());
            } else {
                Object[] arguments = new Object[methodInvocation.getArguments().length - 1];
                int index = 0;
                for (Object argument : methodInvocation.getArguments()) {
                    if (index == paginationIndex) continue;
                    arguments[index++] = argument;
                }
                return doInvoke(SQLUtil.buildPaginationSQL(pagination, sql), arguments);
            }
        }
    }

    abstract protected Object doInvoke(String sql, Object[] arguments);

}

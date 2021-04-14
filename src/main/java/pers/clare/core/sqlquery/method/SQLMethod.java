package pers.clare.core.sqlquery.method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import pers.clare.core.sqlquery.*;
import pers.clare.core.sqlquery.exception.SQLQueryException;
import pers.clare.core.sqlquery.function.ArgumentValueHandler;
import pers.clare.core.sqlquery.page.Pagination;
import pers.clare.core.sqlquery.page.Sort;
import pers.clare.core.sqlquery.util.SQLUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class SQLMethod implements MethodInterceptor {
    protected static final Object[] emptyArguments = new Object[0];
    protected String sql;
    protected SQLStoreService sqlStoreService;
    protected SQLQueryReplaceBuilder sqlQueryReplaceBuilder;
    protected SQLQueryBuilder sqlQueryBuilder;
    protected Map<String, ArgumentValueHandler> replaces;
    protected Map<String, ArgumentValueHandler> values;
    protected Method method;
    protected Parameter[] parameters;
    protected int paginationIndex = -1;
    protected int sortIndex = -1;

    public void setSql(String sql) {
        this.sql = sql;
        char[] cs = sql.toCharArray();
        if (SQLQueryReplaceBuilder.findKeyCount(cs) > 0) {
            sqlQueryReplaceBuilder = new SQLQueryReplaceBuilder(cs);
            this.replaces = new HashMap<>();
            this.values = new HashMap<>();
            int c = 0;
            for (Parameter p : parameters) {
                buildArgumentValueHandler(p, c++);
            }
        } else if (SQLQueryBuilder.findKeyCount(cs) > 0) {
            sqlQueryBuilder = new SQLQueryBuilder(cs);
            this.values = new HashMap<>();
            int c = 0;
            for (Parameter p : parameters) {
                buildArgumentValueHandler(p, c++);
            }
        }
    }

    private void setArgumentValueHandler(String name, ArgumentValueHandler handler) {
        if (sqlQueryReplaceBuilder != null && sqlQueryReplaceBuilder.hasKey(name)) {
            replaces.put(name, handler);
        } else {
            values.put(name, handler);
        }
    }

    private void buildArgumentValueHandler(Parameter p, int index) {
        Class<?> type = p.getType();
        ArgumentValueHandler handler = (arguments) -> arguments[index];
        if (isSimpleType(type)) {
            setArgumentValueHandler(p.getName(), handler);
        } else {
            buildArgumentValueHandler(type, p.getName(), handler);
        }
    }

    private void buildArgumentValueHandler(Class<?> clazz, String key, ArgumentValueHandler handler) {
        Field[] fields = clazz.getDeclaredFields();
        int modifier;
        Class<?> type;
        ArgumentValueHandler fieldHandler;
        for (Field field : fields) {
            modifier = field.getModifiers();
            if (Modifier.isStatic(modifier) || Modifier.isFinal(modifier)) continue;
            type = field.getType();
            field.setAccessible(true);
            fieldHandler = (arguments) -> {
                try {
                    return field.get(handler.apply(arguments));
                } catch (Exception e) {
                    throw new SQLQueryException(e);
                }
            };
            if (isSimpleType(type)) {
                setArgumentValueHandler(key + '.' + field.getName(), fieldHandler);
            } else {
                buildArgumentValueHandler(type, key + '.' + field.getName(), fieldHandler);
            }
        }
    }

    private boolean isSimpleType(Class<?> type) {
        return type.isPrimitive() || type.getName().startsWith("java.lang") || type.isArray() || type == Collection.class || type == Pagination.class || type == Sort.class;
    }

    public void setMethod(Method method) {
        this.method = method;
        this.parameters = method.getParameters();
    }

    public void setPaginationIndex(int paginationIndex) {
        this.paginationIndex = paginationIndex;
    }

    public void setSortIndex(int sortIndex) {
        this.sortIndex = sortIndex;
    }

    public void setSqlStoreService(SQLStoreService sqlStoreService) {
        this.sqlStoreService = sqlStoreService;
    }

    protected String toSql(SQLQueryReplaceBuilder sqlQueryReplaceBuilder, MethodInvocation methodInvocation, Pagination pagination) {
        SQLQueryReplace replace = sqlQueryReplaceBuilder.build();
        Object[] args = methodInvocation.getArguments();
        for (Map.Entry<String, ArgumentValueHandler> entry : replaces.entrySet()) {
            replace.replace(entry.getKey(), (String) entry.getValue().apply(args));
        }
        SQLQuery query = replace.buildQuery();
        for (Map.Entry<String, ArgumentValueHandler> entry : values.entrySet()) {
            query.value(entry.getKey(), entry.getValue().apply(args));
        }
        return query.toString(pagination);
    }

    protected String toSql(SQLQueryBuilder sqlQueryBuilder, MethodInvocation methodInvocation, Pagination pagination) {
        Object[] args = methodInvocation.getArguments();
        SQLQuery query = sqlQueryBuilder.build();
        for (Map.Entry<String, ArgumentValueHandler> entry : values.entrySet()) {
            query.value(entry.getKey(), entry.getValue().apply(args));
        }
        return query.toString(pagination);
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) {
        Pagination pagination = null;
        Sort sort = null;
        if (paginationIndex != -1) {
            pagination = (Pagination) methodInvocation.getArguments()[paginationIndex];
        } else if (sortIndex != -1) {
            sort = (Sort) methodInvocation.getArguments()[sortIndex];
        }
        if (sqlQueryReplaceBuilder != null) {
            return doInvoke(toSql(sqlQueryReplaceBuilder, methodInvocation, pagination), emptyArguments);
        } else if (sqlQueryBuilder != null) {
            return doInvoke(toSql(sqlQueryBuilder, methodInvocation, pagination), emptyArguments);
        } else {
            if (pagination != null) {
                return doInvoke(SQLUtil.buildPaginationSQL(pagination, sql), methodInvocation.getArguments());
            } else if (sort != null) {
                return doInvoke(SQLUtil.buildSortSQL(sort, sql), methodInvocation.getArguments());
            } else {
                return doInvoke(sql, methodInvocation.getArguments());
            }
        }
    }

    abstract protected Object doInvoke(String sql, Object[] arguments);

}

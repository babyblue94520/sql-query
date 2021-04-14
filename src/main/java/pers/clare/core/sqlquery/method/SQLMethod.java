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
    protected Map<String, ArgumentValueHandler> valueHandlers;
    protected Method method;
    protected Parameter[] parameters;
    protected ArgumentValueHandler paginationHandler;
    protected ArgumentValueHandler sortHandler;

    protected Pagination getPagination(Object[] arguments) {
        if (paginationHandler == null) return null;
        return (Pagination) paginationHandler.apply(arguments);
    }

    protected Sort getSort(Object[] arguments) {
        if (sortHandler == null) return null;
        return (Sort) sortHandler.apply(arguments);
    }

    public void setSql(String sql) {
        this.sql = sql;
        char[] cs = sql.toCharArray();
        if (SQLQueryReplaceBuilder.findKeyCount(cs) > 0) {
            sqlQueryReplaceBuilder = new SQLQueryReplaceBuilder(cs);
            this.replaces = new HashMap<>();
        } else if (SQLQueryBuilder.findKeyCount(cs) > 0) {
            sqlQueryBuilder = new SQLQueryBuilder(cs);
        }
        this.valueHandlers = new HashMap<>();
        int c = 0;
        for (Parameter p : parameters) {
            buildArgumentValueHandler(p, c++);
        }
    }

    private void setArgumentValueHandler(String name, ArgumentValueHandler handler) {
        if (replaces != null && sqlQueryReplaceBuilder.hasKey(name)) {
            replaces.put(name, handler);
        } else {
            valueHandlers.put(name, handler);
        }
    }

    private void buildArgumentValueHandler(Parameter p, int index) {
        Class<?> type = p.getType();
        ArgumentValueHandler handler = (arguments) -> arguments[index];
        if (isSimpleType(type)) {
            setArgumentValueHandler(p.getName(), handler);
        } else if (type == Pagination.class) {
            paginationHandler = handler;
        } else if (type == Sort.class) {
            sortHandler = handler;
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
            } else if (type == Pagination.class) {
                paginationHandler = fieldHandler;
            } else if (type == Sort.class) {
                sortHandler = fieldHandler;
            } else {
                buildArgumentValueHandler(type, key + '.' + field.getName(), fieldHandler);
            }
        }
    }

    private boolean isSimpleType(Class<?> type) {
        return type.isPrimitive() || type.getName().startsWith("java.lang") || type.isArray() || type == Collection.class;
    }

    public void setMethod(Method method) {
        this.method = method;
        this.parameters = method.getParameters();
    }

    public void setSqlStoreService(SQLStoreService sqlStoreService) {
        this.sqlStoreService = sqlStoreService;
    }

    protected SQLQuery toSqlQuery(SQLQueryReplaceBuilder sqlQueryReplaceBuilder, Object[] arguments) {
        SQLQueryReplace replace = sqlQueryReplaceBuilder.build();
        for (Map.Entry<String, ArgumentValueHandler> entry : replaces.entrySet()) {
            replace.replace(entry.getKey(), (String) entry.getValue().apply(arguments));
        }
        SQLQuery query = replace.buildQuery();
        for (Map.Entry<String, ArgumentValueHandler> entry : valueHandlers.entrySet()) {
            query.value(entry.getKey(), entry.getValue().apply(arguments));
        }
        return query;
    }

    protected SQLQuery toSqlQuery(SQLQueryBuilder sqlQueryBuilder, Object[] arguments) {
        SQLQuery query = sqlQueryBuilder.build();
        for (Map.Entry<String, ArgumentValueHandler> entry : valueHandlers.entrySet()) {
            query.value(entry.getKey(), entry.getValue().apply(arguments));
        }
        return query;
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) {
        Object[] arguments = methodInvocation.getArguments();
        Pagination pagination = getPagination(arguments);
        Sort sort = getSort(arguments);
        SQLQuery query = null;
        String executeSQL = this.sql;
        if (sqlQueryReplaceBuilder != null) {
            query = toSqlQuery(sqlQueryReplaceBuilder, arguments);
        } else if (sqlQueryBuilder != null) {
            query = toSqlQuery(sqlQueryReplaceBuilder, arguments);
        }
        if (query == null) {
            if (pagination != null) {
                executeSQL = SQLUtil.buildPaginationSQL(pagination, executeSQL);
            } else if (sort != null) {
                executeSQL = SQLUtil.buildSortSQL(sort, executeSQL);
            }
        } else {
            if (pagination != null) {
                executeSQL = query.toString(pagination);
            } else if (sort != null) {
                executeSQL = query.toString(sort);
            } else {
                executeSQL = query.toString();
            }
            arguments = emptyArguments;
        }
        return doInvoke(executeSQL, arguments);
    }

    abstract protected Object doInvoke(String sql, Object[] arguments);

}

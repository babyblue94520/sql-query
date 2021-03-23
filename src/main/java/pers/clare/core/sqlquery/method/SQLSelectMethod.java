package pers.clare.core.sqlquery.method;

import pers.clare.core.sqlquery.SQLStoreService;

import java.lang.reflect.Method;

public abstract class SQLSelectMethod extends SQLMethod {
    protected Class<?> valueType;

    public SQLSelectMethod(Method method, String sql, SQLStoreService sqlStoreService, Class<?> valueType) {
        this(method, sql, sqlStoreService, valueType, -1);
    }

    public SQLSelectMethod(Method method, String sql, SQLStoreService sqlStoreService, Class<?> valueType, int paginationIndex) {
        super(method, sql, sqlStoreService, paginationIndex);
        this.valueType = valueType;
    }

}

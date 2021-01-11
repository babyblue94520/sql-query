package pers.clare.core.sqlquery.handler;

import pers.clare.core.sqlquery.SQLStoreService;

import java.lang.reflect.Method;

public abstract class SQLSelectHandler extends SQLHandler {
    protected Class<?> valueType;

    public SQLSelectHandler(Method method, String sql, SQLStoreService sqlStoreService, Class<?> valueType) {
        super(method, sql, sqlStoreService);
        this.valueType = valueType;
    }
}

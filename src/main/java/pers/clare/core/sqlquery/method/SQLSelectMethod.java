package pers.clare.core.sqlquery.method;

import pers.clare.core.sqlquery.SQLStoreService;

import java.lang.reflect.Method;

public abstract class SQLSelectMethod extends SQLMethod {
    protected Class<?> valueType;

    public SQLSelectMethod(Method method, String sql, SQLStoreService sqlStoreService, Class<?> valueType) {
        super(method, sql, sqlStoreService);
        this.valueType = valueType;
    }
}

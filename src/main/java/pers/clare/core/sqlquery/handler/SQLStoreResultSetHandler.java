package pers.clare.core.sqlquery.handler;

import pers.clare.core.sqlquery.SQLStoreService;

import java.lang.reflect.Method;

public class SQLStoreResultSetHandler extends SQLStoreHandler {

    public SQLStoreResultSetHandler(Method method, String sql, SQLStoreService sqlService, Class<?> valueType) {
        super(method, sql, sqlService, valueType);
    }

    @Override
    protected Object doInvoke(String sql, Object[] arguments) {
        return sqlStoreService.find(sqlStore, sql, arguments);
    }
}

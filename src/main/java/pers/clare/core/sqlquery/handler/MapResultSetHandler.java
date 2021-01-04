package pers.clare.core.sqlquery.handler;

import pers.clare.core.sqlquery.SQLStoreService;

import java.lang.reflect.Method;

public class MapResultSetHandler extends SQLSelectHandler {

    public MapResultSetHandler(Method method, String sql, SQLStoreService sqlStoreService, Class<?> valueType) {
        super(method, sql, sqlStoreService, valueType);
    }

    @Override
    protected Object doInvoke(String sql, Object[] arguments) {
        return sqlStoreService.find(sql, arguments);
    }
}

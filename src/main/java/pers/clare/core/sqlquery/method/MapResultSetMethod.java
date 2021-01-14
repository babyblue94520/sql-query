package pers.clare.core.sqlquery.method;

import pers.clare.core.sqlquery.SQLStoreService;

import java.lang.reflect.Method;

public class MapResultSetMethod extends SQLSelectMethod {

    public MapResultSetMethod(Method method, String sql, SQLStoreService sqlStoreService, Class<?> valueType) {
        super(method, sql, sqlStoreService, valueType);
    }

    @Override
    protected Object doInvoke(String sql, Object[] arguments) {
        return sqlStoreService.find(sql, valueType, arguments);
    }
}

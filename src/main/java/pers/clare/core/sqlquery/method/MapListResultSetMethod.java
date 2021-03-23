package pers.clare.core.sqlquery.method;

import pers.clare.core.sqlquery.SQLStoreService;

import java.lang.reflect.Method;

public class MapListResultSetMethod extends SQLSelectMethod {

    public MapListResultSetMethod(Method method, String sql, SQLStoreService sqlService, Class<?> valueType, int paginationIndex) {
        super(method, sql, sqlService, valueType, paginationIndex);
    }

    @Override
    protected Object doInvoke(String sql, Object[] arguments) {
        return sqlStoreService.findAllMap(valueType, sql, arguments);
    }
}

package pers.clare.core.sqlquery.method;

import pers.clare.core.sqlquery.SQLStoreService;

import java.lang.reflect.Method;

public class ListResultSetMethod extends SQLSelectMethod {

    public ListResultSetMethod(Method method, String sql, SQLStoreService sqlStoreService, Class<?> valueType) {
        super(method, sql, sqlStoreService, valueType);
    }

    @Override
    protected Object doInvoke(String sql, Object[] arguments) {
        return sqlStoreService.findAll(valueType, sql, arguments);
    }
}

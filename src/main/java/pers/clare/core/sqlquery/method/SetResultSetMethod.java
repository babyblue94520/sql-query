package pers.clare.core.sqlquery.method;

import pers.clare.core.sqlquery.SQLStoreService;

import java.lang.reflect.Method;

public class SetResultSetMethod extends SQLSelectMethod {

    public SetResultSetMethod(Method method, String sql, SQLStoreService sqlStoreService, Class<?> valueType) {
        super(method, sql, sqlStoreService, valueType);
    }

    @Override
    protected Object doInvoke(String sql, Object[] arguments) {
        return sqlStoreService.findSet(sql, valueType,arguments);
    }
}

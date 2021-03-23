package pers.clare.core.sqlquery.method;

import pers.clare.core.sqlquery.SQLStoreService;

import java.lang.reflect.Method;

public class SQLStoreResultSetMethod extends SQLStoreMethod {

    public SQLStoreResultSetMethod(Method method, String sql, SQLStoreService sqlStoreService, Class<?> valueType, int paginationIndex) {
        super(method, sql, sqlStoreService, valueType, paginationIndex);
    }

    @Override
    protected Object doInvoke(String sql, Object[] arguments) {
        return sqlStoreService.find(sqlStore, sql, arguments);
    }
}

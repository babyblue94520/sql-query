package pers.clare.core.sqlquery.method;

import pers.clare.core.sqlquery.SQLStoreService;

import java.lang.reflect.Method;

public class SQLStoreSetResultSetMethod extends SQLStoreMethod {

    public SQLStoreSetResultSetMethod(Method method, String sql, SQLStoreService sqlService, Class<?> valueType, int paginationIndex) {
        super(method, sql, sqlService, valueType, paginationIndex);
    }

    @Override
    protected Object doInvoke(String sql, Object[] arguments) {
        return sqlStoreService.findSet(sqlStore, sql, arguments);
    }
}

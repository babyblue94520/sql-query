package pers.clare.core.sqlquery.handler;

import pers.clare.core.sqlquery.SQLService;
import pers.clare.core.sqlquery.SQLStoreService;

import java.lang.reflect.Method;

public class ListResultSetHandler extends SQLSelectHandler  {

    public ListResultSetHandler(Method method, String sql, SQLStoreService sqlStoreService, Class<?> valueType) {
        super(method, sql, sqlStoreService, valueType);
    }

    @Override
    protected Object doInvoke(String sql, Object[] arguments) {
        return sqlStoreService.findAll(valueType, sql, arguments);
    }
}

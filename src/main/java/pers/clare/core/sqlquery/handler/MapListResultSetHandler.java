package pers.clare.core.sqlquery.handler;

import pers.clare.core.sqlquery.SQLService;
import pers.clare.core.sqlquery.SQLStoreService;

import java.lang.reflect.Method;

public class MapListResultSetHandler extends SQLSelectHandler  {

    public MapListResultSetHandler(Method method, String sql, SQLStoreService sqlService, Class<?> valueType) {
        super(method, sql, sqlService, valueType);
    }

    @Override
    protected Object doInvoke(String sql, Object[] arguments) {
        return sqlStoreService.findAllMap(valueType, sql, arguments);
    }
}

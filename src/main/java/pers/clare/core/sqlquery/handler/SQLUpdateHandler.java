package pers.clare.core.sqlquery.handler;

import pers.clare.core.sqlquery.*;

import java.lang.reflect.Method;

public class SQLUpdateHandler extends SQLHandler {

    public SQLUpdateHandler(Method method, String sql, SQLStoreService sqlStoreService) {
        super(method, sql, sqlStoreService);
    }

    @Override
    protected Object doInvoke(String sql, Object[] arguments) {
        return sqlStoreService.update(sql, arguments);
    }


}

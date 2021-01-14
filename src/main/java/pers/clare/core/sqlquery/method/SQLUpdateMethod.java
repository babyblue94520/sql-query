package pers.clare.core.sqlquery.method;

import pers.clare.core.sqlquery.*;

import java.lang.reflect.Method;

public class SQLUpdateMethod extends SQLMethod {

    public SQLUpdateMethod(Method method, String sql, SQLStoreService sqlStoreService) {
        super(method, sql, sqlStoreService);
    }

    @Override
    protected Object doInvoke(String sql, Object[] arguments) {
        return sqlStoreService.update(sql, arguments);
    }


}

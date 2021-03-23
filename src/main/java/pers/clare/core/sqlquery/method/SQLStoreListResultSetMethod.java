package pers.clare.core.sqlquery.method;

import pers.clare.core.sqlquery.SQLStoreService;
import pers.clare.core.sqlquery.SQLUtil;
import pers.clare.core.sqlquery.page.Pagination;

import java.lang.reflect.Method;

public class SQLStoreListResultSetMethod extends SQLStoreMethod {

    public SQLStoreListResultSetMethod(Method method, String sql, SQLStoreService sqlService, Class<?> valueType, int paginationIndex) {
        super(method, sql, sqlService, valueType, paginationIndex);
    }

    @Override
    protected Object doInvoke(String sql, Object[] arguments) {
        return sqlStoreService.findAll(sqlStore, sql, arguments);
    }
}

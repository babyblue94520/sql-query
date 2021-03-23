package pers.clare.core.sqlquery.method;

import pers.clare.core.sqlquery.SQLStore;
import pers.clare.core.sqlquery.SQLStoreFactory;
import pers.clare.core.sqlquery.SQLStoreService;
import pers.clare.core.sqlquery.page.Pagination;

import java.lang.reflect.Method;

public class MapPageMethod extends PageMethod {

    protected Class<?> valueType;

    public MapPageMethod(Method method, String sql, SQLStoreService sqlStoreService, Class<?> valueType, int paginationIndex) {
        super(method, sql, sqlStoreService, paginationIndex);
        this.valueType = valueType;
    }

    protected Object doInvoke(String sql, Pagination pagination, Object[] arguments) {
        return sqlStoreService.page(this.valueType, sql, pagination, arguments);
    }
}

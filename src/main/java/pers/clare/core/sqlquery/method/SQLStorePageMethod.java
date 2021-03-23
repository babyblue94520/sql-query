package pers.clare.core.sqlquery.method;

import pers.clare.core.sqlquery.SQLStore;
import pers.clare.core.sqlquery.SQLStoreFactory;
import pers.clare.core.sqlquery.SQLStoreService;
import pers.clare.core.sqlquery.page.Pagination;

import java.lang.reflect.Method;

public class SQLStorePageMethod extends PageMethod {

    protected SQLStore<?> sqlStore;

    public SQLStorePageMethod(Method method, String sql, SQLStoreService sqlStoreService, Class<?> valueType, int paginationIndex) {
        super(method, sql, sqlStoreService, paginationIndex);
        this.sqlStore = SQLStoreFactory.build(valueType, false);
    }

    protected Object doInvoke(String sql, Pagination pagination, Object[] arguments) {
        return sqlStoreService.page(this.sqlStore, sql, pagination, arguments);
    }
}
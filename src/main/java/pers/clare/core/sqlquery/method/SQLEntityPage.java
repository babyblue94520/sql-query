package pers.clare.core.sqlquery.method;

import pers.clare.core.sqlquery.SQLStore;
import pers.clare.core.sqlquery.SQLStoreFactory;
import pers.clare.core.sqlquery.page.Pagination;


public class SQLEntityPage extends PageMethod {

    protected SQLStore<?> sqlStore;

    SQLEntityPage(Class<?> valueType) {
        this.sqlStore = SQLStoreFactory.build(valueType, false);
    }

    protected Object doInvoke(String sql, Pagination pagination, Object[] arguments) {
        return sqlStoreService.page(this.sqlStore, sql, pagination, arguments);
    }
}

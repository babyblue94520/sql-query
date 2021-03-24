package pers.clare.core.sqlquery.method;

import pers.clare.core.sqlquery.page.Pagination;

public class BasicTypeMapPage extends PageMethod {

    protected Class<?> valueType;

    BasicTypeMapPage(Class<?> valueType) {
        this.valueType = valueType;
    }

    protected Object doInvoke(String sql, Pagination pagination, Object[] arguments) {
        return sqlStoreService.page(this.valueType, sql, pagination, arguments);
    }
}

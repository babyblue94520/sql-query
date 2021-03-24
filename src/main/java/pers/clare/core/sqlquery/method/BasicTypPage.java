package pers.clare.core.sqlquery.method;

import pers.clare.core.sqlquery.page.Pagination;

public class BasicTypPage extends PageMethod {

    protected Class<?> valueType;

    BasicTypPage(Class<?> valueType) {
        this.valueType = valueType;
    }

    protected Object doInvoke(String sql, Pagination pagination, Object[] arguments) {
        return sqlStoreService.basicPage(this.valueType, sql, pagination, arguments);
    }
}

package pers.clare.core.sqlquery.method;

public class SQLUpdateMethod extends SQLMethod {

    @Override
    protected Object doInvoke(String sql, Object[] arguments) {
        return sqlStoreService.update(sql, arguments);
    }
}

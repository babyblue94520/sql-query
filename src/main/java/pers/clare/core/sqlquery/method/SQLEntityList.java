package pers.clare.core.sqlquery.method;

public class SQLEntityList extends SQLStoreMethod {

    SQLEntityList(Class<?> valueType) {
        super(valueType);
    }

    @Override
    protected Object doInvoke(String sql, Object[] arguments) {
        return sqlStoreService.findAll(sqlStore, sql, arguments);
    }
}

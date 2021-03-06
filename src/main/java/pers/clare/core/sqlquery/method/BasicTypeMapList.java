package pers.clare.core.sqlquery.method;

public class BasicTypeMapList extends SQLSelectMethod {

    BasicTypeMapList(Class<?> valueType) {
        super(valueType);
    }

    @Override
    protected Object doInvoke(String sql, Object[] arguments) {
        return sqlStoreService.findAllMap(valueType, sql, arguments);
    }
}

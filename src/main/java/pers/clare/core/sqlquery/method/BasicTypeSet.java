package pers.clare.core.sqlquery.method;


public class BasicTypeSet extends SQLSelectMethod {

    BasicTypeSet(Class<?> valueType) {
        super(valueType);
    }

    @Override
    protected Object doInvoke(String sql, Object[] arguments) {
        return sqlStoreService.findSet(sql, valueType, arguments);
    }
}

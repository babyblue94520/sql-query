package pers.clare.core.sqlquery.method;

import pers.clare.core.sqlquery.SQLStore;
import pers.clare.core.sqlquery.SQLStoreFactory;
import pers.clare.core.sqlquery.SQLStoreService;

import java.lang.reflect.Method;

public abstract class SQLStoreMethod extends SQLMethod {

    protected SQLStore<?> sqlStore;

    public SQLStoreMethod(Method method, String sql, SQLStoreService sqlStoreService, Class<?> valueType) {
        super(method, sql, sqlStoreService);
        this.sqlStore = SQLStoreFactory.build(valueType, false);
    }
}

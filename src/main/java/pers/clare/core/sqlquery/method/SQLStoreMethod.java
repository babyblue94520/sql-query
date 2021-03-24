package pers.clare.core.sqlquery.method;

import pers.clare.core.sqlquery.SQLStore;
import pers.clare.core.sqlquery.SQLStoreFactory;

public abstract class SQLStoreMethod extends SQLMethod {

    protected SQLStore<?> sqlStore;

    SQLStoreMethod(Class<?> valueType) {
        this.sqlStore = SQLStoreFactory.build(valueType, false);
    }
}

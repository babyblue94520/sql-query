package pers.clare.core.sqlquery.handler;

import lombok.AllArgsConstructor;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import pers.clare.core.sqlquery.SQLService;
import pers.clare.core.sqlquery.SQLStore;
import pers.clare.core.sqlquery.SQLStoreService;

@AllArgsConstructor
public class SQLStoreResultSetHandler implements MethodInterceptor {
    private String sql;
    private SQLStoreService sqlStoreService;
    private SQLStore<?> store;

    @Override
    public Object invoke(MethodInvocation methodInvocation) {
        return sqlStoreService.find(store, sql, methodInvocation.getArguments());
    }
}

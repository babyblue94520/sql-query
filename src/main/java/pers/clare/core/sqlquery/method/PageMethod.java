package pers.clare.core.sqlquery.method;

import org.aopalliance.intercept.MethodInvocation;
import pers.clare.core.sqlquery.page.Pagination;

public abstract class PageMethod extends SQLMethod {

    @Override
    public Object invoke(MethodInvocation methodInvocation) {
        Pagination pagination = (Pagination) methodInvocation.getArguments()[paginationIndex];
        if (sqlQueryReplaceBuilder != null) {
            return doInvoke(toSql(sqlQueryReplaceBuilder, methodInvocation, null), pagination, emptyArguments);
        } else if (sqlQueryBuilder != null) {
            return doInvoke(toSql(sqlQueryBuilder, methodInvocation, null), pagination, emptyArguments);
        } else {
            Object[] arguments = new Object[methodInvocation.getArguments().length - 1];
            int index = 0;
            for (Object argument : methodInvocation.getArguments()) {
                if (index == paginationIndex) continue;
                arguments[index++] = argument;
            }
            return doInvoke(sql, pagination, arguments);
        }
    }

    @Override
    protected Object doInvoke(String sql, Object[] arguments) {
        return null;
    }

    abstract protected Object doInvoke(String sql, Pagination pagination, Object[] arguments);
}

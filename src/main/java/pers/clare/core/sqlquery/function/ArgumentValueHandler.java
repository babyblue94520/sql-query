package pers.clare.core.sqlquery.function;

import pers.clare.core.sqlquery.exception.SQLQueryException;

@FunctionalInterface
public interface ArgumentValueHandler {
    Object apply(Object[] arguments) throws SQLQueryException;
}

package pers.clare.core.sqlquery.function;

import pers.clare.core.sqlquery.SQLStore;

import java.sql.ResultSet;

@FunctionalInterface
public interface StoreResultSetHandler<T, R> {
    R apply(ResultSet resultSet, SQLStore<T> sqlStore) throws Exception;
}
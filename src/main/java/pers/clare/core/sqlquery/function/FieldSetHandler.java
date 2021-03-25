package pers.clare.core.sqlquery.function;


import java.sql.ResultSet;

@FunctionalInterface
public interface FieldSetHandler {
   void apply(Object target, ResultSet resultSet, int index) throws Exception;
}

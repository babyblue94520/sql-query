package pers.clare.core.sqlquery.function;

@FunctionalInterface
public interface ResultSetValueConverter<T> {
   T apply(Object value) throws Exception;
}

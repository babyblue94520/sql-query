package pers.clare.core.sqlquery.function;

@FunctionalInterface
public interface FieldGetHandler {
   Object apply(Object target) throws Exception;
}

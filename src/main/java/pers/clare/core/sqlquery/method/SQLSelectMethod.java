package pers.clare.core.sqlquery.method;

public abstract class SQLSelectMethod extends SQLMethod {
    protected Class<?> valueType;

    SQLSelectMethod(Class<?> valueType) {
        this.valueType = valueType;
    }
}

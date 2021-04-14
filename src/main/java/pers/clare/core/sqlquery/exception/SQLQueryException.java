package pers.clare.core.sqlquery.exception;

public class SQLQueryException extends RuntimeException {
    public SQLQueryException(String message) {
        super(message);
    }

    public SQLQueryException(String message, Throwable cause) {
        super(message, cause);
    }

    public SQLQueryException(Throwable cause) {
        super(cause);
    }
}

package pers.clare.core.sqlquery.support;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import pers.clare.core.sqlquery.exception.SQLQueryException;

import java.util.Stack;

@Log4j2
public class ConnectionReuseManager {

    private final Stack<ConnectionReuse> stack = new Stack<>();

    private ConnectionReuse prev = null;

    @Getter
    private ConnectionReuse current = null;


    public void init(boolean transaction, int isolation, boolean readonly) {
        if (transaction && readonly) {
            throw new SQLQueryException("Cannot execute statement in a READ ONLY transaction.");
        }
        stack.push(current);
        if (current == null) {
            current = new ConnectionReuse(transaction, isolation, readonly);
        } else {
            if (transaction != current.transaction || isolation != current.isolation) {
                current = new ConnectionReuse(transaction, isolation, readonly);
            }
        }

        prev = null;
    }

    public ConnectionReuse get() {
        return current;
    }

    public void commit() {
        if (check()) {
            prev.commit();
        }
    }

    public void rollback() {
        if (check()) {
            prev.rollback();
        }
    }

    public void close() {
        if (check()) {
            prev.close();
        }
        prev = null;
    }

    private boolean check() {
        if (stack.size() == 0) return false;
        if (prev == null) {
            prev = current;
            current = stack.pop();
        }
        return prev != null && (prev != current || stack.size() <= 0);
    }
}

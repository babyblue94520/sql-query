package pers.clare.core.sqlquery.support;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.Stack;

@Log4j2
public class ConnectionReuseManager {

    private Stack<ConnectionReuse> stack = new Stack<>();

    private ConnectionReuse prev = null;

    @Getter
    private ConnectionReuse current = null;


    public void init(boolean transaction) {
        stack.push(current);
        if (current == null || (current.isTransaction() != transaction)) {
            current = new ConnectionReuse(transaction);
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
        if (prev == null || (prev == current&&stack.size()>0)) return false;
        return true;
    }
}

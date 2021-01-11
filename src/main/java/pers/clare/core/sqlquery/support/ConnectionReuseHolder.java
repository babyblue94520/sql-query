package pers.clare.core.sqlquery.support;

public class ConnectionReuseHolder {

    private static final ThreadLocal<ConnectionReuse> cache = new NamedThreadLocal<>("Connection Cache Holder");

    ConnectionReuseHolder() {
    }

    public static ConnectionReuse get() {
        ConnectionReuse cc = cache.get();
        if (cc == null) cache.set((cc = new ConnectionReuse()));
        return cc;
    }

    static class NamedThreadLocal<T> extends ThreadLocal<T> {
        private final String name;

        public NamedThreadLocal(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }
}



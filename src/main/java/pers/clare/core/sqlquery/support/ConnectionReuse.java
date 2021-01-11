package pers.clare.core.sqlquery.support;


import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class ConnectionReuse implements AutoCloseable {
    private final Map<DataSource, ConnectionCache> connections = new HashMap<>();
    private final Stack<ConnectionCache> stack = new Stack<>();

    ConnectionReuse() {
    }

    public Connection getConnection(DataSource dataSource) throws SQLException {
        ConnectionCache connectionCache = connections.get(dataSource);
        if (connectionCache == null) {
            connections.put(dataSource, connectionCache = new ConnectionCache(dataSource));
        }
        stack.push(connectionCache);
        return connectionCache.open();
    }

    @Override
    public void close() throws Exception {
        if (stack.size() > 0) {
            stack.pop().close();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        System.out.println("finalize");
        for (ConnectionCache connectionCache : connections.values()) {
            try {
                connectionCache.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        connections.clear();
        stack.clear();
    }
}

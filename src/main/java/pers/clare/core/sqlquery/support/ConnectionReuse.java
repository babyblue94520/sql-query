package pers.clare.core.sqlquery.support;


import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ConnectionReuse implements AutoCloseable {
    private final Map<DataSource, ConnectionCache> connections = new HashMap<>();
    private boolean reuse = false;
    private boolean transaction = false;
    private boolean readonly = true;

    ConnectionReuse() {
    }

    public Connection getConnection(DataSource dataSource) throws SQLException {
        if (reuse) {
            ConnectionCache connectionCache = connections.get(dataSource);
            if (connectionCache == null) {
                connections.put(dataSource, connectionCache = new ConnectionCache(dataSource));
            }
            return connectionCache.open(transaction);
        } else {
            return dataSource.getConnection();
        }
    }

    public void commit() {
        if (!transaction) return;
        for (ConnectionCache connectionCache : connections.values()) {
            try {
                connectionCache.commit();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void rollback() {
        if (!transaction) return;
        for (ConnectionCache connectionCache : connections.values()) {
            try {
                connectionCache.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void close() throws Exception {
        for (ConnectionCache connectionCache : connections.values()) {
            try {
                connectionCache.close(transaction);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void finalize() throws Throwable {
        System.out.println("finalize");
        for (ConnectionCache connectionCache : connections.values()) {
            try {
                connectionCache.close(transaction);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        connections.clear();
    }

    public boolean isReuse() {
        return reuse;
    }

    public void setReuse(boolean reuse) {
        this.reuse = reuse;
    }

    public boolean isTransaction() {
        return transaction;
    }

    public void setTransaction(boolean transaction) {
        this.transaction = transaction;
    }

    public boolean isReadonly() {
        return readonly;
    }

    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
    }
}

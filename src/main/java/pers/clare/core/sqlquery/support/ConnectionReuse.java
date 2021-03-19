package pers.clare.core.sqlquery.support;


import lombok.extern.log4j.Log4j2;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Log4j2
public class ConnectionReuse implements AutoCloseable {
    private final Map<DataSource, ConnectionCache> connections = new HashMap<>();
    private boolean transaction;
    private Integer isolation = null;
    private boolean readonly = false;

    ConnectionReuse() {
        this(false, false);
    }

    ConnectionReuse(boolean transaction) {
        this(transaction, false);
    }

    ConnectionReuse(boolean transaction, boolean readonly) {
        this.transaction = transaction;
        this.readonly = readonly;
    }


    public Connection getConnection(DataSource dataSource) throws SQLException {
        ConnectionCache connectionCache = connections.get(dataSource);
        if (connectionCache == null) {
            connections.put(dataSource, connectionCache = new ConnectionCache(dataSource));
        }
        return connectionCache.open(transaction);
    }

    public void commit() {
        if (!transaction) return;
        for (ConnectionCache connectionCache : connections.values()) {
            try {
                connectionCache.commit();
            } catch (SQLException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    public void rollback() {
        if (!transaction) return;
        for (ConnectionCache connectionCache : connections.values()) {
            try {
                connectionCache.rollback();
            } catch (SQLException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public void close() {
        for (ConnectionCache connectionCache : connections.values()) {
            try {
                connectionCache.close(transaction);
            } catch (SQLException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    @Override
    protected void finalize() throws Throwable {
        close();
        connections.clear();
    }

    public boolean isTransaction() {
        return transaction;
    }

    public boolean isReadonly() {
        return readonly;
    }

    public Integer getIsolation() {
        return isolation;
    }
}

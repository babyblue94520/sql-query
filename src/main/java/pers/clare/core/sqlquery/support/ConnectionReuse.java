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
    boolean transaction;
    int isolation;
    boolean readonly;

    ConnectionReuse(boolean transaction, int isolation, boolean readonly) {
        this.transaction = transaction;
        this.isolation = isolation;
        this.readonly = readonly;
    }

    public Connection getConnection(DataSource dataSource) throws SQLException {
        ConnectionCache connectionCache = connections.get(dataSource);
        if (connectionCache == null) {
            connections.put(dataSource, connectionCache = new ConnectionCache(transaction, isolation, dataSource));
        }
        return connectionCache.open();
    }

    public void commit() {
        for (ConnectionCache connectionCache : connections.values()) {
            try {
                connectionCache.commit();
            } catch (SQLException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    public void rollback() {
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
                connectionCache.close();
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

    public boolean isReadonly() {
        return readonly;
    }
}

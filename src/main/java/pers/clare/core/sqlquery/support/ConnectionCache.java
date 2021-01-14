package pers.clare.core.sqlquery.support;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;


public class ConnectionCache {
    private DataSource dataSource;
    private Connection connection;
    private boolean autocommit = true;

    public ConnectionCache(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Connection open(boolean transaction) throws SQLException {
        if (connection == null) {
            connection = dataSource.getConnection();
            if (transaction) {
                autocommit = connection.getAutoCommit();
                connection.setAutoCommit(false);
            }
        }
        return connection;
    }

    public void rollback() throws SQLException {
        connection.rollback();
    }

    public void commit() throws SQLException {
        connection.commit();
    }

    public void close(boolean transaction) throws SQLException {
        if (connection == null) return;
        if (transaction) {
            connection.setAutoCommit(autocommit);
        }
        connection.close();
        connection = null;

    }
}

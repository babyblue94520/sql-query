package pers.clare.core.sqlquery.support;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;


public class ConnectionCache {
    private DataSource dataSource;
    private Connection connection;
    private int count = 0;

    public ConnectionCache(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Connection open() throws SQLException {
        if (count++ == 0) {
            connection = dataSource.getConnection();
        }
        return connection;
    }

    public void close() throws SQLException {
        if (--count == 0) {
            connection.close();
            connection = null;
        }
    }
}

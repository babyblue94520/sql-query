package pers.clare.core.sqlquery;

import lombok.extern.log4j.Log4j2;
import pers.clare.core.sqlquery.exception.SQLQueryException;
import pers.clare.core.sqlquery.function.ResultSetHandler;
import pers.clare.core.sqlquery.page.Page;
import pers.clare.core.sqlquery.page.Pagination;
import pers.clare.core.sqlquery.support.ConnectionReuse;
import pers.clare.core.sqlquery.support.ConnectionReuseHolder;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Log4j2
public class SQLService {
    protected DataSource write;
    protected DataSource read;

    protected boolean hasRead;

    public SQLService(DataSource write) {
        this.write = write;
        this.read = write;
    }

    public SQLService(
            DataSource write
            , DataSource read
    ) {
        this.write = write;
        this.read = read;
        hasRead = write != read;
    }

    public DataSource getDataSource(boolean readonly) {
        return readonly ? read : write;
    }

    protected <T> boolean retry(T result, boolean readonly) {
        return result == null && readonly && hasRead;
    }


    protected void close(ConnectionReuse connectionReuse, Connection connection) {
        try {
            if (!connectionReuse.isReuse() && connection != null) {
                connection.close();
            }
        } catch (Exception e) {
            throw new SQLQueryException(e.getMessage(), e);
        }
    }

    protected ResultSet go(Connection connection, String sql, Object[] parameters) throws SQLException {
        if (parameters.length == 0) {
            return connection.createStatement().executeQuery(sql);
        } else {
            PreparedStatement ps = connection.prepareStatement(sql);
            PreparedStatementUtil.setValue(ps, parameters);
            return ps.executeQuery();
        }
    }

    protected <T, R> R queryHandler(
            Boolean readonly
            , String sql
            , Class<T> valueType
            , Object[] parameters
            , ResultSetHandler<T, R> resultSetHandler
    ) {
        ConnectionReuse connectionReuse = ConnectionReuseHolder.get();
        if (readonly && !connectionReuse.isReadonly()) {
            readonly = false;
        }
        Connection connection = null;
        try {
            connection = connectionReuse.getConnection(getDataSource(readonly));
            return doQueryHandler(connection, readonly, sql, valueType, parameters, resultSetHandler);
        } catch (SQLQueryException e) {
            throw e;
        } catch (Exception e) {
            throw new SQLQueryException(e.getMessage(), e);
        } finally {
            close(connectionReuse, connection);
        }
    }

    private <T, R> R doQueryHandler(
            Connection connection
            , Boolean readonly
            , String sql
            , Class<T> valueType
            , Object[] parameters
            , ResultSetHandler<T, R> resultSetHandler
    ) throws Exception {
        R result = resultSetHandler.apply(go(connection, sql, parameters), valueType);
        if (retry(result, readonly)) {
            return doQueryHandler(connection, false, sql, valueType, parameters, resultSetHandler);
        } else {
            return result;
        }
    }

    private final ResultSetHandler findHandler = this::findHandler;

    private <T> T findHandler(ResultSet rs, Class<T> valueType) throws Exception {
        return ResultSetUtil.to(valueType, rs);
    }

    private final ResultSetHandler findMapHandler = this::findMapHandler;

    private <T> Map<String, T> findMapHandler(ResultSet rs, Class<T> valueType) throws Exception {
        return ResultSetUtil.toMap(valueType, rs);
    }

    private final ResultSetHandler findSetHandler = this::findSetHandler;

    private <T> Set<T> findSetHandler(ResultSet rs, Class<T> valueType) throws Exception {
        return ResultSetUtil.toSet(valueType, rs);
    }

    private final ResultSetHandler findAllMapHandler = this::findAllMapHandler;

    private <T> List<Map<String, T>> findAllMapHandler(ResultSet rs, Class<T> valueType) throws Exception {
        return ResultSetUtil.toMapList(valueType, rs);
    }

    private final ResultSetHandler findAllHandler = this::findAllHandler;

    private <T> List<T> findAllHandler(ResultSet rs, Class<T> valueType) throws Exception {
        return ResultSetUtil.toList(valueType, rs);
    }

    private final ResultSetHandler pageHandler = this::pageHandler;

    private <T> List<T> pageHandler(ResultSet rs, Class<T> valueType) throws Exception {
        return ResultSetUtil.toList(valueType, rs);
    }

    public <T> Map<String, T> find(
            String sql
            , Class<T> valueType
            , Object... parameters
    ) {
        return (Map<String, T>) queryHandler(false, sql, valueType, parameters, findMapHandler);
    }

    public <T> Map<String, T> find(
            boolean readonly
            , String sql
            , Class<T> valueType
            , Object... parameters
    ) {
        return (Map<String, T>) queryHandler(readonly, sql, valueType, parameters, findMapHandler);
    }

    public <T> Set<T> findSet(
            String sql
            , Class<T> valueType
            , Object... parameters
    ) {
        return (Set<T>) queryHandler(false, sql, valueType, parameters, findSetHandler);
    }

    public <T> Set<T> findSet(
            boolean readonly
            , String sql
            , Class<T> valueType
            , Object... parameters
    ) {
        return (Set<T>) queryHandler(readonly, sql, valueType, parameters, findSetHandler);
    }

    public <T> T findFirst(
            Class<T> valueType
            , String sql
            , Object... parameters
    ) {
        return (T) queryHandler(false, sql, valueType, parameters, findHandler);
    }

    public <T> T findFirst(
            boolean readonly
            , Class<T> valueType
            , String sql
            , Object... parameters
    ) {
        return (T) queryHandler(readonly, sql, valueType, parameters, findHandler);
    }

    public <T> List<Map<String, T>> findAllMap(
            Class<T> valueType
            , String sql
            , Object... parameters
    ) {
        return (List<Map<String, T>>) queryHandler(false, sql, valueType, parameters, findAllMapHandler);
    }

    public <T> List<Map<String, T>> findAllMap(
            boolean readonly
            , Class<T> valueType
            , String sql
            , Object... parameters
    ) {
        return (List<Map<String, T>>) queryHandler(readonly, sql, valueType, parameters, findAllMapHandler);
    }

    public <T> List<T> findAll(
            Class<T> valueType
            , String sql
            , Object... parameters
    ) {
        return (List<T>) queryHandler(false, sql, valueType, parameters, findAllHandler);
    }

    public <T> List<T> findAll(
            boolean readonly
            , Class<T> valueType
            , String sql
            , Object... parameters
    ) {
        return (List<T>) queryHandler(readonly, sql, valueType, parameters, findAllHandler);
    }

    public <T> T insert(
            String sql
            , Class<T> keyType
            , Object... parameters
    ) {
        ConnectionReuse connectionReuse = ConnectionReuseHolder.get();
        Connection connection = null;
        try {
            connection = connectionReuse.getConnection(write);
            Statement statement;
            if (parameters.length == 0) {
                statement = connection.createStatement();
                statement.execute(sql, Statement.RETURN_GENERATED_KEYS);
            } else {
                statement = PreparedStatementUtil.executeInsert(connection, sql, parameters);
            }
            if (statement.getUpdateCount() == 0) return null;
            ResultSet rs = statement.getGeneratedKeys();
            return rs.next() ? rs.getObject(1, keyType) : null;
        } catch (SQLException e) {
            throw new SQLQueryException(e.getMessage(), e);
        } finally {
            close(connectionReuse, connection);
        }
    }

    public int update(
            String sql
            , Object... parameters
    ) {
        ConnectionReuse connectionReuse = ConnectionReuseHolder.get();
        Connection connection = null;
        try {
            connection = connectionReuse.getConnection(write);
            if (parameters.length == 0) {
                return connection.createStatement().executeUpdate(sql);
            } else {
                PreparedStatement ps = connection.prepareStatement(sql);
                PreparedStatementUtil.setValue(ps, parameters);
                return ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new SQLQueryException(e.getMessage(), e);
        } finally {
            close(connectionReuse, connection);
        }
    }

    public <T> Page<Map<String, T>> page(
            Class<T> clazz
            , String sql
            , Pagination pagination
            , Object... parameters
    ) {
        return page(false, clazz, sql, pagination, parameters);
    }

    public <T> Page<Map<String, T>> page(
            boolean readonly
            , Class<T> clazz
            , String sql
            , Pagination pagination
            , Object... parameters
    ) {
        ConnectionReuse connectionReuse = ConnectionReuseHolder.get();
        if (readonly && !connectionReuse.isReadonly()) {
            readonly = false;
        }
        Connection connection = null;
        try {
            connection = connectionReuse.getConnection(getDataSource(readonly));
            List<Map<String, T>> list = ResultSetUtil.toMapList(clazz, go(connection, SQLUtil.buildPaginationSQL(pagination, sql), parameters));
            long total = list.size();
            if (total == pagination.getSize()) {
                ResultSet rs = go(connection, SQLUtil.buildTotalSQL(sql), parameters);
                if (rs.next()) {
                    total = rs.getLong(1);
                } else {
                    throw new SQLQueryException("query total error");
                }
            }
            return Page.of(pagination.getPage(), pagination.getSize(), list, total);
        } catch (SQLQueryException e) {
            throw e;
        } catch (Exception e) {
            throw new SQLQueryException(e.getMessage(), e);
        } finally {
            close(connectionReuse, connection);
        }
    }
}

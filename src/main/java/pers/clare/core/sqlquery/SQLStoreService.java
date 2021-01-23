package pers.clare.core.sqlquery;

import lombok.extern.log4j.Log4j2;
import pers.clare.core.sqlquery.exception.SQLQueryException;
import pers.clare.core.sqlquery.function.StoreResultSetHandler;
import pers.clare.core.sqlquery.page.Page;
import pers.clare.core.sqlquery.page.Pagination;
import pers.clare.core.sqlquery.support.ConnectionReuse;
import pers.clare.core.sqlquery.support.ConnectionReuseHolder;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import java.util.Set;


@Log4j2
public class SQLStoreService extends SQLService {

    public SQLStoreService(DataSource write) {
        super(write);
    }

    public SQLStoreService(DataSource write, DataSource read) {
        super(write, read);
    }

    private <T, R> R queryHandler(
            Boolean readonly
            , SQLStore<T> sqlStore
            , String sql
            , Object[] parameters
            , StoreResultSetHandler<T, R> storeResultSetHandler
    ) {
        ConnectionReuse connectionReuse = ConnectionReuseHolder.get();
        if (readonly && !connectionReuse.isReadonly()) {
            readonly = false;
        }
        Connection connection = null;
        try {
            connection = connectionReuse.getConnection(getDataSource(readonly));
            return doQueryHandler(connection, readonly, sqlStore, sql, parameters, storeResultSetHandler);
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
            , SQLStore<T> sqlStore
            , String sql
            , Object[] parameters
            , StoreResultSetHandler<T, R> storeResultSetHandler
    ) throws Exception {
        R result = storeResultSetHandler.apply(go(connection, sql, parameters), sqlStore);
        if (retry(result, readonly)) {
            return queryHandler(false, sqlStore, sql, parameters, storeResultSetHandler);
        } else {
            return result;
        }
    }

    private <T, R> R queryHandler(
            Boolean readonly
            , SQLStore<T> sqlStore
            , T entity
            , StoreResultSetHandler<T, R> storeResultSetHandler
    ) {
        ConnectionReuse connectionReuse = ConnectionReuseHolder.get();
        if (readonly && !connectionReuse.isReadonly()) {
            readonly = false;
        }
        Connection connection = null;
        try {
            connection = connectionReuse.getConnection(getDataSource(readonly));
            return doQueryHandler(connection, readonly, sqlStore, entity, storeResultSetHandler);
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
            , SQLStore<T> sqlStore
            , T entity
            , StoreResultSetHandler<T, R> storeResultSetHandler
    ) throws Exception {
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(SQLUtil.setValue(sqlStore.selectById, sqlStore.keyFields, entity));
        R result = storeResultSetHandler.apply(rs, sqlStore);
        if (retry(result, readonly)) {
            statement.close();
            return doQueryHandler(connection, false, sqlStore, entity, storeResultSetHandler);
        } else {
            return result;
        }
    }

    private final StoreResultSetHandler findHandler = this::findHandler;

    private <T> T findHandler(ResultSet rs, SQLStore<T> sqlStore) throws Exception {
        return SQLUtil.toInstance(sqlStore.constructorMap, rs);
    }

    private final StoreResultSetHandler findSetHandler = this::findSetHandler;

    private <T> Set<T> findSetHandler(ResultSet rs, SQLStore<T> sqlStore) throws Exception {
        return SQLUtil.toSetInstance(sqlStore.constructorMap, rs);
    }

    private final StoreResultSetHandler findAllHandler = this::findAllHandler;

    private <T> List<T> findAllHandler(ResultSet rs, SQLStore<T> sqlStore) throws Exception {
        return SQLUtil.toInstances(sqlStore.constructorMap, rs);
    }

    public <T> T find(
            SQLStore<T> store
            , T entity
    ) {
        return (T) queryHandler(false, store, entity, findHandler);
    }

    public <T> T find(
            boolean readonly
            , SQLStore<T> sqlStore
            , T entity
    ) {
        return (T) queryHandler(readonly, sqlStore, entity, findHandler);
    }

    public <T> T find(
            SQLStore<T> sqlStore
            , String sql
            , Object... parameters
    ) {
        return (T) queryHandler(false, sqlStore, sql, parameters, findHandler);
    }

    public <T> T find(
            boolean readonly
            , SQLStore<T> sqlStore
            , String sql
            , Object... parameters
    ) {
        return (T) queryHandler(readonly, sqlStore, sql, parameters, findHandler);

    }

    public <T> Set<T> findSet(
            SQLStore<T> sqlStore
            , String sql
            , Object... parameters
    ) {
        return (Set<T>) queryHandler(false, sqlStore, sql, parameters, findSetHandler);
    }

    public <T> Set<T> findSet(
            boolean readonly
            , SQLStore<T> sqlStore
            , String sql
            , Object... parameters
    ) {
        return (Set<T>) queryHandler(readonly, sqlStore, sql, parameters, findSetHandler);
    }

    public <T> List<T> findAll(
            SQLStore<T> sqlStore
            , String sql
            , Object... parameters
    ) {
        return (List<T>) queryHandler(false, sqlStore, sql, parameters, findAllHandler);
    }

    public <T> List<T> findAll(
            boolean readonly
            , SQLStore<T> sqlStore
            , String sql
            , Object... parameters
    ) {
        return (List<T>) queryHandler(readonly, sqlStore, sql, parameters, findAllHandler);
    }

    public <T> Page<T> page(
            SQLStore<T> sqlStore
            , String sql
            , Pagination pagination
            , Object... parameters
    ) {
        return page(false, sqlStore, sql, pagination, parameters);
    }

    public <T> Page<T> page(
            boolean readonly
            , SQLStore<T> sqlStore
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
            List<T> list = SQLUtil.toInstances(sqlStore.constructorMap, go(connection, SQLUtil.buildPaginationSQL(pagination, sql), parameters));
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

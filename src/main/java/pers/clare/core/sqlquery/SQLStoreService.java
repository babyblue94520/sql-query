package pers.clare.core.sqlquery;

import lombok.extern.log4j.Log4j2;
import pers.clare.core.sqlquery.exception.SQLQueryException;
import pers.clare.core.sqlquery.function.StoreResultSetHandler;
import pers.clare.core.sqlquery.page.Page;
import pers.clare.core.sqlquery.page.Pagination;

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
        Connection connection = null;
        try {
            connection = getConnection(readonly);
            return doQueryHandler(connection, readonly, sqlStore, sql, parameters, storeResultSetHandler);
        } catch (SQLQueryException e) {
            throw e;
        } catch (Exception e) {
            throw new SQLQueryException(e.getMessage(), e);
        } finally {
            close(connection);
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
            , SQLCrudStore<T> sqlStore
            , T entity
            , StoreResultSetHandler<T, R> storeResultSetHandler
    ) {
        Connection connection = null;
        try {
            connection = getConnection(readonly);
            return doQueryHandler(connection, readonly, sqlStore, entity, storeResultSetHandler);
        } catch (SQLQueryException e) {
            throw e;
        } catch (Exception e) {
            throw new SQLQueryException(e.getMessage(), e);
        } finally {
            close(connection);
        }
    }

    private <T, R> R doQueryHandler(
            Connection connection
            , Boolean readonly
            , SQLCrudStore<T> sqlStore
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

    private <T> T findHandler(ResultSet rs, SQLStore<T> sqlStore) throws Exception {
        return SQLUtil.toInstance(sqlStore, rs);
    }

    private <T> Set<T> findSetHandler(ResultSet rs, SQLStore<T> sqlStore) throws Exception {
        return SQLUtil.toSetInstance(sqlStore, rs);
    }

    private <T> List<T> findAllHandler(ResultSet rs, SQLStore<T> sqlStore) throws Exception {
        return SQLUtil.toInstances(sqlStore, rs);
    }

    public <T> T find(
            SQLCrudStore<T> store
            , T entity
    ) {
        return queryHandler(false, store, entity, this::findHandler);
    }

    public <T> T find(
            boolean readonly
            , SQLCrudStore<T> sqlStore
            , T entity
    ) {
        return queryHandler(readonly, sqlStore, entity, this::findHandler);
    }

    public <T> T find(
            SQLStore<T> sqlStore
            , String sql
            , Object... parameters
    ) {
        return queryHandler(false, sqlStore, sql, parameters, this::findHandler);
    }

    public <T> T find(
            boolean readonly
            , SQLStore<T> sqlStore
            , String sql
            , Object... parameters
    ) {
        return queryHandler(readonly, sqlStore, sql, parameters, this::findHandler);

    }

    public <T> Set<T> findSet(
            SQLStore<T> sqlStore
            , String sql
            , Object... parameters
    ) {
        return queryHandler(false, sqlStore, sql, parameters, this::findSetHandler);
    }

    public <T> Set<T> findSet(
            boolean readonly
            , SQLStore<T> sqlStore
            , String sql
            , Object... parameters
    ) {
        return queryHandler(readonly, sqlStore, sql, parameters, this::findSetHandler);
    }

    public <T> List<T> findAll(
            SQLStore<T> sqlStore
            , String sql
            , Object... parameters
    ) {
        return queryHandler(false, sqlStore, sql, parameters, this::findAllHandler);
    }

    public <T> List<T> findAll(
            boolean readonly
            , SQLStore<T> sqlStore
            , String sql
            , Object... parameters
    ) {
        return queryHandler(readonly, sqlStore, sql, parameters, this::findAllHandler);
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
        Connection connection = null;
        try {
            connection = getConnection(readonly);
            List<T> list = SQLUtil.toInstances(sqlStore, go(connection, SQLUtil.buildPaginationSQL(pagination, sql), parameters));
            long total = list.size();
            if (total == pagination.getSize()) total = getTotal(connection, sql, parameters);
            return Page.of(pagination.getPage(), pagination.getSize(), list, total);
        } catch (SQLQueryException e) {
            throw e;
        } catch (Exception e) {
            throw new SQLQueryException(e.getMessage(), e);
        } finally {
            close(connection);
        }
    }
}

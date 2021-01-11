package pers.clare.core.sqlquery;

import lombok.extern.log4j.Log4j2;
import pers.clare.core.sqlquery.exception.SQLQueryException;

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

    public <T> T find(
            SQLStore<T> store
            , T entity
    ) {
        return find(false, store, entity);
    }

    public <T> T find(
            boolean readonly
            , SQLStore<T> store
            , T entity
    ) {
        try (
                Connection conn = getDataSource(readonly).getConnection();
        ) {
            ResultSet rs = conn.createStatement().executeQuery(SQLUtil.setValue(store.selectById, store.keyFields, entity));
            T result = SQLUtil.toInstance(store.constructorMap, rs);
            if (retry(result, readonly)) {
                return find(false, store, entity);
            }
            return result;
        } catch (Exception e) {
            throw new SQLQueryException(e.getMessage(), e);
        }
    }

    public <T> T find(
            SQLStore<T> store
            , String sql
            , Object... parameters
    ) {
        return find(false, store, sql, parameters);
    }

    public <T> T find(
            boolean readonly
            , SQLStore<T> store
            , String sql
            , Object... parameters
    ) {
        try (
                Connection conn = getDataSource(readonly).getConnection();
        ) {
            ResultSet rs;
            if (parameters.length == 0) {
                rs = conn.createStatement().executeQuery(sql);
            } else {
                PreparedStatement ps = conn.prepareStatement(sql);
                PreparedStatementUtil.setValue(ps, parameters);
                rs = ps.executeQuery();
            }
            T result = SQLUtil.toInstance(store.constructorMap, rs);
            if (retry(result, readonly)) {
                return find(false, store, sql, parameters);
            }
            return result;
        } catch (Exception e) {
            throw new SQLQueryException(e.getMessage(), e);
        }
    }

    public <T> Set<T> findSet(
            SQLStore<T> store
            , String sql
            , Object... parameters
    ) {
        return findSet(false, store, sql, parameters);
    }

    public <T> Set<T> findSet(
            boolean readonly
            , SQLStore<T> store
            , String sql
            , Object... parameters
    ) {
        try (
                Connection conn = getDataSource(readonly).getConnection();
        ) {
            ResultSet rs;
            if (parameters.length == 0) {
                rs = conn.createStatement().executeQuery(sql);
            } else {
                PreparedStatement ps = conn.prepareStatement(sql);
                PreparedStatementUtil.setValue(ps, parameters);
                rs = ps.executeQuery();
            }
            Set<T> result = SQLUtil.toSetInstance(store.constructorMap, rs);
            if (retry(result, readonly)) {
                return findSet(false, store, sql, parameters);
            }
            return result;
        } catch (Exception e) {
            throw new SQLQueryException(e.getMessage(), e);
        }
    }

    public <T> List<T> findAll(
            SQLStore<T> store
            , String sql
            , Object... parameters
    ) {
        return findAll(false, store, sql, parameters);
    }

    public <T> List<T> findAll(
            boolean readonly
            , SQLStore<T> store
            , String sql
            , Object... parameters
    ) {
        try (
                Connection conn = getDataSource(readonly).getConnection();
        ) {
            ResultSet rs;
            if (parameters.length == 0) {
                rs = conn.createStatement().executeQuery(sql);
            } else {
                PreparedStatement ps = conn.prepareStatement(sql);
                PreparedStatementUtil.setValue(ps, parameters);
                rs = ps.executeQuery();
            }
            List<T> result = SQLUtil.toInstances(store.constructorMap, rs);
            if (retry(result, readonly)) {
                return findAll(false, store, sql, parameters);
            }
            return result;
        } catch (Exception e) {
            throw new SQLQueryException(e.getMessage(), e);
        }
    }
}

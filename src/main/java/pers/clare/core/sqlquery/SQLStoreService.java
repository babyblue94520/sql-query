package pers.clare.core.sqlquery;

import lombok.extern.log4j.Log4j2;
import pers.clare.core.sqlquery.exception.SQLQueryException;

import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;
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
            , String sql
            , Object[] parameters
    ) {
        return find(false, store, sql, parameters);
    }

    public <T> T find(
            boolean readonly
            , SQLStore<T> store
            , String sql
            , Object[] parameters
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
            T result = SQLEntityUtil.toInstance(store.constructorMap, rs);
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
            , Object[] parameters
    ) {
        return findSet(false, store, sql, parameters);
    }

    public <T> Set<T> findSet(
            boolean readonly
            , SQLStore<T> store
            , String sql
            , Object[] parameters
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
            Set<T> result = SQLEntityUtil.toSetInstance(store.constructorMap, rs);
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
            , Object[] parameters
    ) {
        return findAll(false, store, sql, parameters);
    }

    public <T> List<T> findAll(
            boolean readonly
            , SQLStore<T> store
            , String sql
            , Object[] parameters
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
            List<T> result = SQLEntityUtil.toInstances(store.constructorMap, rs);
            if (retry(result, readonly)) {
                return findAll(false, store, sql, parameters);
            }
            return result;
        } catch (Exception e) {
            throw new SQLQueryException(e.getMessage(), e);
        }
    }

    public <T> T insert(
            T entity
    ) {
        try (
                Connection conn = write.getConnection();
        ) {
            SQLStore<T> store = SQLStoreFactory.find((Class<T>) entity.getClass());
            PreparedStatement ps = conn.prepareStatement(store.insert, Statement.RETURN_GENERATED_KEYS);
            SQLEntityUtil.setValue(ps, entity, store.insertMethods);
            ps.executeUpdate();
            if (store.autoKey != null) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    store.autoKey.set(entity, rs.getObject(1, store.autoKey.getType()));
                }
            }
            return entity;
        } catch (SQLException | IllegalAccessException | InvocationTargetException e) {
            throw new SQLQueryException(e.getMessage(), e);
        }
    }

    public <T> int update(
            T entity
    ) {
        try (
                Connection conn = write.getConnection();
        ) {
            SQLStore<T> store = SQLStoreFactory.find((Class<T>) entity.getClass());
            PreparedStatement ps = conn.prepareStatement(store.update);
            int index = SQLEntityUtil.setValue(ps, entity, store.updateMethods, 1);
            SQLEntityUtil.setValue(ps, entity, store.keyMethods, index);
            return ps.executeUpdate();
        } catch (SQLException | IllegalAccessException | InvocationTargetException e) {
            throw new SQLQueryException(e.getMessage(), e);
        }
    }

    public <T> int delete(
            T entity
    ) {
        try (
                Connection conn = write.getConnection();
        ) {
            SQLStore<T> store = SQLStoreFactory.find((Class<T>) entity.getClass());
            PreparedStatement ps = conn.prepareStatement(store.delete);
            SQLEntityUtil.setValue(ps, entity, store.keyMethods);
            return ps.executeUpdate();
        } catch (SQLException | IllegalAccessException | InvocationTargetException e) {
            throw new SQLQueryException(e.getMessage(), e);
        }
    }

    public <T> int delete(
            Class<T> clazz
            , Object... args
    ) {
        try (
                Connection conn = write.getConnection();
        ) {
            SQLStore<T> store = SQLStoreFactory.find(clazz);
            PreparedStatement ps = conn.prepareStatement(store.delete);
            PreparedStatementUtil.setValue(ps, args);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new SQLQueryException(e.getMessage(), e);
        }
    }
}

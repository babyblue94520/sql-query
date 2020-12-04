package pers.clare.core.sqlquery;

import pers.clare.core.sqlquery.exception.SQLQueryException;
import pers.clare.core.sqlquery.jpa.SQLEntityRepository;

import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;

public class SQLEntityRepositoryImpl<T, ID> extends SQLService implements SQLEntityRepository<T, ID> {
    private final Class<T> entityClass;

    public SQLEntityRepositoryImpl(Class<T> entityClass, DataSource write) {
        super(write);
        this.entityClass = entityClass;
    }

    public SQLEntityRepositoryImpl(Class<T> entityClass, DataSource write, DataSource read) {
        super(write, read);
        this.entityClass = entityClass;
    }

    public <T> long count() {
        return count(false);
    }

    public <T> long count(
            boolean readonly
    ) {
        SQLStore<T> store = (SQLStore<T>) SQLStoreFactory.find(entityClass);
        try (
                Connection conn = getDataSource(readonly).getConnection();
        ) {
            PreparedStatement ps = conn.prepareStatement(store.count);
            Long count = ResultSetUtil.to(Long.class, ps.executeQuery());
            if (retry(count, readonly)) {
                return count(false);
            }
            return count == null ? 0L : count;
        } catch (Exception e) {
            throw new SQLQueryException(e.getMessage(), e);
        }
    }

    public <T> T find(
            T entity
    ) {
        return find(false, entity);
    }

    public <T> T find(
            boolean readonly
            , T entity
    ) {
        try (
                Connection conn = getDataSource(readonly).getConnection();
        ) {
            SQLStore<T> store = (SQLStore<T>) SQLStoreFactory.find(entityClass);
            PreparedStatement ps = conn.prepareStatement(store.select);
            SQLEntityUtil.setValue(ps, entity, store.keyMethods);
            T result = SQLEntityUtil.toInstance(store.constructorMap, ps.executeQuery());
            if (retry(result, readonly)) {
                return find(false, entity);
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
            SQLStore<T> store = (SQLStore<T>) SQLStoreFactory.find(entityClass);
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
            SQLStore<T> store = (SQLStore<T>) SQLStoreFactory.find(entityClass);
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
            SQLStore<T> store = (SQLStore<T>) SQLStoreFactory.find(entityClass);
            PreparedStatement ps = conn.prepareStatement(store.delete);
            SQLEntityUtil.setValue(ps, entity, store.keyMethods);
            return ps.executeUpdate();
        } catch (SQLException | IllegalAccessException | InvocationTargetException e) {
            throw new SQLQueryException(e.getMessage(), e);
        }
    }

    public <T> int delete(
            Object... args
    ) {
        try (
                Connection conn = write.getConnection();
        ) {
            SQLStore<T> store = (SQLStore<T>) SQLStoreFactory.find(entityClass);
            PreparedStatement ps = conn.prepareStatement(store.delete);
            PreparedStatementUtil.setValue(ps, args);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new SQLQueryException(e.getMessage(), e);
        }
    }
}

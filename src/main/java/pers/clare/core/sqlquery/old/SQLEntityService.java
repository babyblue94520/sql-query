package pers.clare.core.sqlquery.old;

import lombok.extern.log4j.Log4j2;
import pers.clare.core.sqlquery.*;
import pers.clare.core.sqlquery.exception.SQLQueryException;

import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;


@Log4j2
public class SQLEntityService extends SQLService {

    public SQLEntityService(DataSource write) {
        super(write);
    }

    public SQLEntityService(DataSource write, DataSource read) {
        super(write, read);
    }

    public <T> long count(
            T entity
    ) {
        return count(false, entity);
    }

    public <T> long count(
            boolean readonly
            , T entity
    ) {
        SQLStore<T> store = SQLStoreFactory.find((Class<T>) entity.getClass());
        try (
                Connection conn = getDataSource(readonly).getConnection();
        ) {
            PreparedStatement ps = conn.prepareStatement(store.count);
            SQLEntityUtil.setValue(ps, entity, store.keyMethods);
            Long count = ResultSetUtil.to(Long.class, ps.executeQuery());
            if (retry(count, readonly)) {
                return count(false, entity);
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
            SQLStore<T> store = SQLStoreFactory.find((Class<T>) entity.getClass());
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

    public <T> T find(
            Class<T> clazz
            , Object... args
    ) {
        return find(false, clazz, args);
    }

    public <T> T find(
            boolean readonly
            , Class<T> clazz
            , Object... args
    ) {
        try (
                Connection conn = getDataSource(readonly).getConnection();
        ) {
            SQLStore<T> store = SQLStoreFactory.find(clazz);
            PreparedStatement ps = conn.prepareStatement(store.select);
            PreparedStatementUtil.setValue(ps, args);
            T result = SQLEntityUtil.toInstance(store.constructorMap, ps.executeQuery());
            if (retry(result, readonly)) {
                return find(false, clazz, args);
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
            ,Object ...args
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

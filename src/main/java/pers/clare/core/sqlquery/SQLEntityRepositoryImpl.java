package pers.clare.core.sqlquery;

import pers.clare.core.sqlquery.exception.SQLQueryException;
import pers.clare.core.sqlquery.jpa.SQLEntityRepository;

import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.*;

public class SQLEntityRepositoryImpl<T, ID> extends SQLService implements SQLEntityRepository<T, ID> {
    private final SQLStore<T> sqlStore;

    public SQLEntityRepositoryImpl(Class<T> repositoryClass, DataSource write) {
        this(repositoryClass, write, write);
    }

    public SQLEntityRepositoryImpl(Class<T> repositoryClass, DataSource write, DataSource read) {
        super(write, read);

        Type[] interfaces = repositoryClass.getGenericInterfaces();
        if (interfaces == null || interfaces.length == 0) {
            throw new IllegalArgumentException("Repository interface must not be null!");
        }
        ParameterizedType type = (ParameterizedType) interfaces[0];
        Type[] types = type.getActualTypeArguments();
        if (types == null || types.length == 0) {
            throw new IllegalArgumentException("Repository entity class must not be null!");
        }
        sqlStore = SQLStoreFactory.build((Class<T>) types[0], true);
    }

    public long count() {
        return count(false);
    }

    public long count(
            Boolean readonly
    ) {
        try (
                Connection conn = getDataSource(readonly).getConnection();
        ) {
            PreparedStatement ps = conn.prepareStatement(sqlStore.count);
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
            Boolean readonly
            , T entity
    ) {
        try (
                Connection conn = getDataSource(readonly).getConnection();
        ) {
            PreparedStatement ps = conn.prepareStatement(sqlStore.select);
            SQLEntityUtil.setValue(ps, entity, sqlStore.keyMethods);
            T result = (T) SQLEntityUtil.toInstance(sqlStore.constructorMap, ps.executeQuery());
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

            PreparedStatement ps = conn.prepareStatement(sqlStore.insert, Statement.RETURN_GENERATED_KEYS);
            SQLEntityUtil.setValue(ps, entity, sqlStore.insertMethods);
            ps.executeUpdate();
            if (sqlStore.autoKey != null) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    sqlStore.autoKey.set(entity, rs.getObject(1, sqlStore.autoKey.getType()));
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

            PreparedStatement ps = conn.prepareStatement(sqlStore.update);
            int index = SQLEntityUtil.setValue(ps, entity, sqlStore.updateMethods, 1);
            SQLEntityUtil.setValue(ps, entity, sqlStore.keyMethods, index);
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

            PreparedStatement ps = conn.prepareStatement(sqlStore.delete);
            SQLEntityUtil.setValue(ps, entity, sqlStore.keyMethods);
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

            PreparedStatement ps = conn.prepareStatement(sqlStore.delete);
            PreparedStatementUtil.setValue(ps, args);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new SQLQueryException(e.getMessage(), e);
        }
    }
}

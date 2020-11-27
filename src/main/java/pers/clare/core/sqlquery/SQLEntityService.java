package pers.clare.core.sqlquery;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldNameConstants;
import lombok.extern.log4j.Log4j2;
import pers.clare.core.sqlquery.exception.SQLQueryException;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
            T obj
    ) {
        return count(obj.getClass());
    }

    public <T> long count(
            Class<T> clazz
            , Object... args
    ) {
        return count(SQLStoreFactory.find(clazz), args);
    }

    public <T> long count(
            SQLStore<T> store
            , Object... args
    ) {
        Long count = findFirst(Long.class, store.count, args);
        return count == null ? 0 : count;
    }

    public <T> T find(
            Class<T> clazz
            , Object... args
    ) {
        try (
                Connection conn = write.getConnection();
        ) {
            SQLStore<T> store = SQLStoreFactory.find(clazz);
            PreparedStatement ps = conn.prepareStatement(store.select);
            PreparedStatementUtil.setValue(ps, args);
            return SQLEntityUtil.toInstance(store.constructorMap, ps.executeQuery());
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
            SQLStore<T> store = (SQLStore<T>) SQLStoreFactory.find(entity.getClass());
            PreparedStatement ps;
//            if (store.autoKey == null) {
//                ps = conn.prepareStatement(store.insert);
//            } else {
                ps = conn.prepareStatement(store.insert, Statement.RETURN_GENERATED_KEYS);
//            }
            SQLEntityUtil.setValue(ps, entity, store.insertMethods);
            ps.executeUpdate();
            if (store.autoKey != null) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    boolean accessible = store.autoKey.canAccess(entity);
                    store.autoKey.setAccessible(true);
                    store.autoKey.set(entity, rs.getObject(1, store.autoKey.getType()));
                    store.autoKey.setAccessible(accessible);
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
            SQLStore<T> store = (SQLStore<T>) SQLStoreFactory.find(entity.getClass());
            PreparedStatement ps = conn.prepareStatement(store.update);
            SQLEntityUtil.setValue(ps, entity, store.updateMethods);
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
            SQLStore<T> store = (SQLStore<T>) SQLStoreFactory.find(entity.getClass());
            PreparedStatement ps = conn.prepareStatement(store.delete);
            SQLEntityUtil.setValue(ps, entity, store.deleteMethods);
            return ps.executeUpdate();
        } catch (SQLException | IllegalAccessException | InvocationTargetException e) {
            throw new SQLQueryException(e.getMessage(), e);
        }
    }

    public static void main(String[] args) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, NoSuchFieldException {
        Test test = new Test("test");
        for (int i = 0; i < 10; i++) {
            run(test);
        }
    }

    public static void run(Test test) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        int max = 100000;
        Object name;
        Method method = test.getClass().getMethod("getName");
        Field field = test.getClass().getDeclaredField("name");
        field.setAccessible(true);
        long t = System.currentTimeMillis();
        for (int i = 0; i < max; i++) {
            name = test.getName();
        }
        System.out.println(System.currentTimeMillis() - t);
        t = System.currentTimeMillis();
        for (int i = 0; i < max; i++) {
            name = test.name;
        }
        System.out.println(System.currentTimeMillis() - t);
        t = System.currentTimeMillis();
        for (int i = 0; i < max; i++) {
            name = field.get(test);
        }
        System.out.println(System.currentTimeMillis() - t);
        t = System.currentTimeMillis();
        for (int i = 0; i < max; i++) {
            name = method.invoke(test);
        }
        System.out.println(System.currentTimeMillis() - t);
        System.out.println("====================");
    }

    @Getter
    @AllArgsConstructor
    @FieldNameConstants
    static class Test {
        private String name;
    }
}

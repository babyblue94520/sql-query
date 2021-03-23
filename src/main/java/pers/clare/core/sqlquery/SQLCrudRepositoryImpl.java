package pers.clare.core.sqlquery;

import pers.clare.core.sqlquery.exception.SQLQueryException;
import pers.clare.core.sqlquery.page.Next;
import pers.clare.core.sqlquery.page.Page;
import pers.clare.core.sqlquery.page.Pagination;
import pers.clare.core.sqlquery.repository.SQLCrudRepository;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public class SQLCrudRepositoryImpl<T> implements SQLCrudRepository<T> {
    private final SQLStore<T> sqlStore;
    private final SQLStoreService sqlStoreService;

    public SQLCrudRepositoryImpl(Class<T> repositoryClass, SQLStoreService sqlStoreService) {
        this.sqlStoreService = sqlStoreService;
        Type[] interfaces = repositoryClass.getGenericInterfaces();
        if (interfaces.length == 0) {
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
        Long count = sqlStoreService.findFirst(readonly, Long.class, sqlStore.count);
        return count == null ? 0 : count;
    }

    public long count(T entity) {
        return count(false, entity);
    }

    public long count(
            Boolean readonly
            , T entity
    ) {
        try {
            Long count = sqlStoreService.findFirst(readonly, Long.class, SQLUtil.setValue(sqlStore.countById, sqlStore.keyFields, entity));
            return count == null ? 0 : count;
        } catch (Exception e) {
            throw new SQLQueryException(e.getMessage(), e);
        }
    }

    public long countById(Object... ids) {
        return countById(false, ids);
    }

    public long countById(
            Boolean readonly
            , Object... ids
    ) {
        try {
            Long count = sqlStoreService.findFirst(readonly, Long.class, SQLUtil.setValue(sqlStore.countById, sqlStore.keyFields, ids));
            return count == null ? 0 : count;
        } catch (Exception e) {
            throw new SQLQueryException(e.getMessage(), e);
        }
    }

    public List<T> findAll(
    ) {
        return findAll(false);
    }

    @Override
    public Page<T> page(Pagination pagination) {
        return sqlStoreService.page(sqlStore, sqlStore.select, pagination);
    }

    @Override
    public Next<T> next(Pagination pagination) {
        // TODO
        return null;
    }

    public List<T> findAll(
            Boolean readonly
    ) {
        return sqlStoreService.findAll(readonly, sqlStore, sqlStore.select);
    }

    public T findById(Object... ids) {
        return findById(false, ids);
    }

    public T findById(
            Boolean readonly
            , Object... ids
    ) {
        return sqlStoreService.find(readonly, sqlStore, SQLUtil.setValue(sqlStore.selectById, sqlStore.keyFields, ids));
    }

    public T find(
            T entity
    ) {
        return find(false, entity);
    }

    public T find(
            Boolean readonly
            , T entity
    ) {
        return sqlStoreService.find(readonly, sqlStore, entity);
    }

    public T insert(
            T entity
    ) {
        try {
            if (sqlStore.autoKey == null) {
                sqlStoreService.update(SQLUtil.setValue(sqlStore.insert, sqlStore.insertFields, entity));
            } else {
                Object keyValue = sqlStore.autoKey.get(entity);
                String sql;
                if (keyValue == null) {
                    sql = SQLUtil.setValue(sqlStore.insert, sqlStore.insertFields, entity);
                } else {
                    SQLQuery sqlQuery = sqlStore.insertAutoKey.build();
                    sqlQuery.value(sqlStore.autoKey.getName(), keyValue);
                    for (Field f : sqlStore.insertFields) {
                        sqlQuery.value(f.getName(), f.get(entity));
                    }
                    sql = sqlQuery.toString();
                }
                Object key = sqlStoreService.insert(sql, sqlStore.autoKey.getType());
                if (key != null) {
                    sqlStore.autoKey.set(entity, key);
                }
            }
            return entity;
        } catch (IllegalAccessException e) {
            throw new SQLQueryException(e.getMessage(), e);
        }
    }

    public int update(
            T entity
    ) {
        return sqlStoreService.update(SQLUtil.setValue2(sqlStore.update, sqlStore.updateFields, sqlStore.keyFields, entity));
    }

    public int delete(
            T entity
    ) {
        return sqlStoreService.update(SQLUtil.setValue(sqlStore.deleteById, sqlStore.keyFields, entity));
    }

    public int deleteById(
            Object... ids
    ) {
        return sqlStoreService.update(SQLUtil.setValue(sqlStore.deleteById, sqlStore.keyFields, ids));
    }

    public int deleteAll() {
        return sqlStoreService.update(sqlStore.deleteAll);
    }

}

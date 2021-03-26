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
    private final SQLCrudStore<T> sqlStore;
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
        sqlStore = (SQLCrudStore<T>) SQLStoreFactory.build((Class<T>) types[0], true);
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
            String sql = toInsertSQL(sqlStore, entity);
            Field autoKey = sqlStore.autoKey;
            if (autoKey == null) {
                sqlStoreService.update(sql);
            } else {
                Object key = sqlStoreService.insert(sql, autoKey.getType());
                if (key != null) {
                    autoKey.set(entity, key);
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
        try {
            return sqlStoreService.update(toUpdateSQL(sqlStore, entity));
        } catch (IllegalAccessException e) {
            throw new SQLQueryException(e.getMessage(), e);
        }
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

    private static String toInsertSQL(SQLCrudStore sqlStore, Object entity) throws IllegalAccessException {
        FieldColumn[] fieldColumns = sqlStore.fieldColumns;
        StringBuilder columns = new StringBuilder("insert into " + sqlStore.tableName + "(");
        StringBuilder values = new StringBuilder("values(");
        Object value;
        for (FieldColumn fieldColumn : fieldColumns) {
            if (fieldColumn == null || !fieldColumn.isInsertable()) continue;
            value = fieldColumn.getField().get(entity);
            if (value == null) {
                if (fieldColumn.isAuto()) continue;
                if (!fieldColumn.isNullable()) continue;
            }
            columns.append(fieldColumn.getColumnName())
                    .append(',');

            SQLUtil.appendValue(values, value);
            values.append(',');
        }
        values.deleteCharAt(values.length() - 1).append(')');
        columns.deleteCharAt(columns.length() - 1)
                .append(')')
                .append(values);
        return columns.toString();
    }

    private static String toUpdateSQL(SQLCrudStore sqlStore, Object entity) throws IllegalAccessException {
        FieldColumn[] fieldColumns = sqlStore.fieldColumns;
        StringBuilder values = new StringBuilder("update " + sqlStore.tableName + " set ");
        StringBuilder wheres = new StringBuilder(" where ");
        Object value;
        for (FieldColumn fieldColumn : fieldColumns) {
            if (fieldColumn == null) continue;
            value = fieldColumn.getField().get(entity);
            if (fieldColumn.isId()) {
                if (value == null) {
                    wheres.append(fieldColumn.getColumnName())
                            .append(" is null");
                } else {
                    wheres.append(fieldColumn.getColumnName())
                            .append('=');
                    SQLUtil.appendValue(wheres, value);
                }
                wheres.append(" and ");
            } else {
                if (!fieldColumn.isUpdatable()) continue;
                if (value == null && !fieldColumn.isNullable()) continue;

                values.append(fieldColumn.getColumnName())
                        .append('=');
                SQLUtil.appendValue(values, value);
                values.append(',');
            }
        }
        wheres.delete(wheres.length() - 5, wheres.length() - 1);
        values.deleteCharAt(values.length() - 1)
                .append(wheres);
        return values.toString();
    }
}

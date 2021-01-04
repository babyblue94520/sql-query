package pers.clare.core.sqlquery.jpa;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;
import pers.clare.core.sqlquery.*;
import pers.clare.core.sqlquery.exception.SQLQueryException;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.List;

@NoRepositoryBean
public interface SQLEntityRepository<T, ID> extends Repository<T, ID> {

    public long count();

    public long count(Boolean readonly);

    public <T> List<T> findAll();

    public <T> List<T> findAll(Boolean readonly);

    public <T> T find(T entity);

    public <T> T find(
            Boolean readonly
            , T entity
    );

    public <T> T insert(
            T entity
    );

    public <T> int update(
            T entity
    );

    public <T> int delete(
            T entity
    );
}

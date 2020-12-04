package pers.clare.core.sqlquery.jpa;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;
import pers.clare.core.sqlquery.*;
import pers.clare.core.sqlquery.exception.SQLQueryException;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;

@NoRepositoryBean
public interface SQLEntityRepository<T, ID> extends Repository<T, ID> {

    public <T> long count();

    public <T> long count(boolean readonly);

    public <T> T find(T entity);

    public <T> T find(
            boolean readonly
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

    public <T> int delete(
            Object... args
    );
}

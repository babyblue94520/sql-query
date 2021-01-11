package pers.clare.core.sqlquery.jpa;

import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface SQLCrudRepository<T> extends SQLRepository {

    long count();

    long count(Boolean readonly);

    long count(T entity);

    long count(Boolean readonly, T entity);

    long countById(Object... ids);

    long countById(Boolean readonly, Object... ids);

    List<T> findAll();

    List<T> findAll(Boolean readonly);

    T find(T entity);

    T find(Boolean readonly, T entity);

    T findById(Object... ids);

    T findById(Boolean readonly, Object... ids);

    T insert(T entity);

    int update(T entity);

    int delete(T entity);

    int deleteById(Object... ids);

    int deleteAll();
}

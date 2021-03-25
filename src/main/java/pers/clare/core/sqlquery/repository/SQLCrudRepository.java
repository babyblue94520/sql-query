package pers.clare.core.sqlquery.repository;

import org.springframework.data.repository.NoRepositoryBean;
import pers.clare.core.sqlquery.page.Next;
import pers.clare.core.sqlquery.page.Page;
import pers.clare.core.sqlquery.page.Pagination;

import java.util.List;

@NoRepositoryBean
public interface SQLCrudRepository<T> extends SQLRepository {

    long count();

    long count(T entity);

    long countById(Object... keys);

    List<T> findAll();

    Page<T> page(Pagination pagination);

    Next<T> next(Pagination pagination);

    T find(T entity);

    T findById(Object... keys);

    T insert(T entity);

    int update(T entity);

    int delete(T entity);

    int deleteById(Object... keys);

    int deleteAll();
}

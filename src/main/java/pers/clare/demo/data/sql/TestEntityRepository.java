package pers.clare.demo.data.sql;

import org.springframework.data.jpa.repository.Query;
import pers.clare.core.sqlquery.jpa.SQLEntityRepository;
import pers.clare.demo.data.entity.Test;


public interface TestEntityRepository extends SQLEntityRepository<Test, Long> {

    @Query(value = "select 1", nativeQuery = true)
    public Integer aaa();
}

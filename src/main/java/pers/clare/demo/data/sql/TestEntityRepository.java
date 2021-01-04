package pers.clare.demo.data.sql;

import org.springframework.data.jpa.repository.Query;
import pers.clare.core.sqlquery.annotation.Sql;
import pers.clare.core.sqlquery.jpa.SQLEntityRepository;
import pers.clare.demo.data.entity.Test;

import java.util.List;
import java.util.Map;


public interface TestEntityRepository extends SQLEntityRepository<Test, Long> {

    @Sql(query = "select 1")
    public Map<String,Object> aaa();
    @Sql(query = "select * from test where id = :id")
    public Test bbb(Long id);
}

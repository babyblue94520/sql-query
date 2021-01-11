package pers.clare.demo.data.sql;

import pers.clare.core.sqlquery.annotation.Sql;
import pers.clare.core.sqlquery.jpa.SQLCrudRepository;
import pers.clare.demo.data.entity.Test;

import java.util.Map;


public interface TestCrudRepository extends SQLCrudRepository<Test> {

    @Sql(query = "select 1")
    public Map<String,Object> aaa();
    @Sql(query = "select * from test where id = :id")
    public Test find(Long id);
}

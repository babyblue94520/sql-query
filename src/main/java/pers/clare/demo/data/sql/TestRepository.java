package pers.clare.demo.data.sql;

import pers.clare.core.sqlquery.annotation.Sql;
import pers.clare.core.sqlquery.jpa.SQLCrudRepository;
import pers.clare.core.sqlquery.jpa.SQLRepository;
import pers.clare.demo.data.entity.Test;

import java.util.Map;


public interface TestRepository extends SQLRepository {

    @Sql(query = "select 1")
    public Map<String,Object> aaa();
    @Sql(query = "select * from test where id = :id")
    public Test bbb(Long id);
    @Sql(query = "select @id:= :id")
    public Integer define(int id);
    @Sql(query = "select @id")
    public Integer id();
}

package pers.clare.demo.data.sql;

import pers.clare.core.sqlquery.annotation.Sql;
import pers.clare.core.sqlquery.repository.SQLRepository;

public interface TransactionRepository extends SQLRepository {

    // mysql
    @Sql("update user set name = if(@name:=name,:name,:name) where id=:id")
    int updateName(Long id, String name);

    @Sql("select @name")
    String getOldName();
}

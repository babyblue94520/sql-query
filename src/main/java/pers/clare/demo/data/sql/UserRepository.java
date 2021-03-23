package pers.clare.demo.data.sql;

import pers.clare.core.sqlquery.annotation.Sql;
import pers.clare.core.sqlquery.page.Page;
import pers.clare.core.sqlquery.page.Pagination;
import pers.clare.core.sqlquery.repository.SQLCrudRepository;
import pers.clare.demo.data.entity.User;

public interface UserRepository extends SQLCrudRepository<User> {
    @Sql(query = "select * from user where create_time between :startTime and :endTime")
    Page<User> page(Pagination pagination, Long startTime, Long endTime);
}

package pers.clare.demo.data.sql;

import pers.clare.core.sqlquery.jpa.SQLCrudRepository;
import pers.clare.demo.data.entity.User;

public interface UserCrudRepository extends SQLCrudRepository<User> {
}

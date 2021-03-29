package pers.clare.demo.data.sql;

import org.springframework.stereotype.Repository;
import pers.clare.core.sqlquery.repository.SQLCrudRepository;
import pers.clare.demo.data.entity.User;

@Repository
public interface UserRepository extends SQLCrudRepository<User> {
}

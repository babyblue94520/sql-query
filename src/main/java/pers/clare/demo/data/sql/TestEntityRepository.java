package pers.clare.demo.data.sql;

import pers.clare.core.sqlquery.jpa.SQLEntityRepository;
import pers.clare.demo.data.entity.Test;

public interface TestEntityRepository extends SQLEntityRepository<Test,Long> {
}

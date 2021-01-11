package pers.clare.demo.data.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import pers.clare.core.data.repository.ExtendedRepository;
import pers.clare.demo.data.entity.Test;
import pers.clare.demo.data.entity.User;


public interface TestJpaRepository extends ExtendedRepository<Test, Long>, JpaSpecificationExecutor<Test> {
}

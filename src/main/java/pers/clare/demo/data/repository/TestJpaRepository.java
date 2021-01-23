package pers.clare.demo.data.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import pers.clare.core.data.repository.ExtendedRepository;
import pers.clare.demo.data.entity.TestUser;


public interface TestJpaRepository extends ExtendedRepository<TestUser, Long>, JpaSpecificationExecutor<TestUser> {
}

package pers.clare.demo.data.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import pers.clare.core.data.repository.ExtendedRepository;
import pers.clare.demo.data.entity.Test;


public interface TestRepository extends ExtendedRepository<Test, Long>, JpaSpecificationExecutor<Test> {

   public Test findByName(String name);
}

package pers.clare.demo.data.repository;

import pers.clare.core.data.repository.ExtendedRepository;
import pers.clare.demo.data.entity.User;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface UserRepository extends ExtendedRepository<User, Long>, JpaSpecificationExecutor<User> {
}

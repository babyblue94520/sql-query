package pers.clare.demo.data.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import pers.clare.core.data.repository.ExtendedRepository;
import pers.clare.demo.data.entity.User;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface UserRepository extends ExtendedRepository<User, Long>, JpaSpecificationExecutor<User> {

    @Transactional
    @Modifying
    @Query("update User set name=:name where id=:id")
    public int updateName(Long id,String name);

    @Transactional
    @Modifying
    @Query(value = "insert user(account,name)values(?,?)",nativeQuery = true)
    public int insert(String id,String name);
}

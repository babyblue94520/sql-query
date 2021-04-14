package pers.clare.demo.data.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import pers.clare.demo.data.entity.User;

public interface UserJpaRepository extends JpaRepository<User,Long> {
}

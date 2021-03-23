package pers.clare.demo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import pers.clare.demo.data.entity.User;
import pers.clare.demo.data.jpa.UserJpaRepository;


/**
 * 使用者服務
 */
@Slf4j
@Validated
@Service
public class UserJpaService {

    @Autowired
    private UserJpaRepository userJpaRepository;

    public User insert(
            User user
    ) {
        long t = System.currentTimeMillis();
        user.setUpdateTime(t);
        user.setUpdateUser(1L);
        user.setCreateTime(t);
        user.setCreateUser(1L);
        userJpaRepository.insert(user);
        return userJpaRepository.insert(user);
    }

    public User update(
            User user
    ) {
        return userJpaRepository.update(user);
    }

}

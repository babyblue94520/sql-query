package pers.clare.demo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import pers.clare.core.sqlquery.exception.SQLQueryException;
import pers.clare.core.sqlquery.page.Page;
import pers.clare.core.sqlquery.page.Pagination;
import pers.clare.demo.data.entity.User;
import pers.clare.demo.data.sql.UserRepository;

import java.util.List;


/**
 * 使用者服務
 */
@Slf4j
@Validated
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Page<User> page(Pagination pagination) {
        return userRepository.page(pagination);
    }

    public User insert(
            User user
    ) {
        long t = System.currentTimeMillis();
        user.setUpdateTime(t);
        user.setUpdateUser(1L);
        user.setCreateTime(t);
        user.setCreateUser(1L);
        return userRepository.insert(user);
    }

    public int update(
            User user
    ) {
        return userRepository.update(user);
    }

}

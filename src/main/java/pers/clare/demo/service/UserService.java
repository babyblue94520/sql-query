package pers.clare.demo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import pers.clare.core.sqlquery.*;
import pers.clare.core.sqlquery.annotation.Sql;
import pers.clare.demo.data.entity.User;
import pers.clare.demo.data.repository.UserRepository;
import pers.clare.demo.data.sql.UserCrudRepository;


/**
 * 使用者服務
 */
@Slf4j
@Validated
@Service
public class UserService {
    /**
     * 密碼最小長度.
     */
    private static final int PASSWORD_MIN_LENGTH = 8;

    @Sql
    private SQLQueryReplaceBuilder findAll;

    @Autowired
    private UserRepository userRepository;

    {
        SQLInjector.inject(this);
    }

    /**
     * 分頁查詢使用者
     *
     * @param pageable the pageable
     * @param id       the id
     * @param name     the name
     * @param roleId   roleId
     * @return the page
     */
    public Page<User> findAll(
            Pageable pageable
            , Integer id
            , String name
            , Integer roleId
            , String account
    ) {
        SQLQuery query = findAll.build()
                .replace("id", id == null ? "" : "and u.id = :id")
                .replace("name", StringUtils.isEmpty(name) ? "" : "and u.name like :name")
                .replace("roleId", StringUtils.isEmpty(roleId) ? "" : "and u.role_id = :roleId")
                .replace("account", StringUtils.isEmpty(account) ? "" : "and u.account like :account")
                .buildQuery()
                .value("id", id)
                .value("name", name)
                .value("roleId", roleId)
                .value("account", account);
        return null;
    }

    public User insert(
            User user
    ) {

        long t = System.currentTimeMillis();
        user.setUpdateTime(t);
        user.setUpdateUser(1L);
        user.setCreateTime(t);
        user.setCreateUser(1L);
        user.setLoginFailCount(0);
        userRepository.insert(user);
        return userRepository.insert(user);
    }

    public User update(
            User user
    ) {
        return userRepository.update(user);
    }

}

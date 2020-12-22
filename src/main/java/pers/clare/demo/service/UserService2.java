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


/**
 * 使用者服務
 */
@Slf4j
@Validated
@Service
public class UserService2 {
    /**
     * 密碼最小長度.
     */
    private static final int PASSWORD_MIN_LENGTH = 8;

    @Sql
    private SQLQueryReplaceBuilder findAll;
    @Sql
    private SQLQueryReplaceBuilder findAll4;
    @Sql
    private SQLQueryReplaceBuilder findAll5;
    @Sql
    private SQLQueryReplaceBuilder findAll6;
    @Sql
    private SQLQueryReplaceBuilder findAll7;
    @Sql
    private SQLQueryReplaceBuilder findAll8;
    @Sql
    private SQLQueryReplaceBuilder findAll9;
    @Sql
    private SQLQueryReplaceBuilder findAll10;

    @Autowired
    private SQLEntityService sqlEntityService;

    @Autowired
    private SQLQueryService sqlQueryService;

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
        return sqlQueryService.findAll(User.class, query, pageable);
    }

    public User find(Long id) {
        return sqlEntityService.find(User.class, id);
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
        sqlEntityService.insert(user);
        return sqlEntityService.find(User.class, user.getId());
    }

    public int update(
            User user
    ) {
        return sqlEntityService.update(user);
    }

}

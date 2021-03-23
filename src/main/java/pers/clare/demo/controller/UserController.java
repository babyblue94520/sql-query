package pers.clare.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.clare.core.sqlquery.page.Page;
import pers.clare.core.sqlquery.page.Pagination;
import pers.clare.demo.data.entity.User;
import pers.clare.demo.data.sql.UserRepository;

@RequestMapping("user")
@RestController
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("page")
    public Page<User> page(
            Pagination pagination
            , Long startTime
            , Long endTime
    ) throws Exception {
        return userRepository.page(pagination, startTime, endTime);
    }

}

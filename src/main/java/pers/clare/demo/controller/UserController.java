package pers.clare.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.clare.core.sqlquery.SQLQueryService;
import pers.clare.demo.data.entity.User;
import pers.clare.demo.data.repository.UserRepository;
import pers.clare.demo.service.UserService;

import java.util.List;

@RequestMapping("user")
@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private SQLQueryService sqlQueryService;

//    @GetMapping
//    public List<User> findAll() {
//        return userService.findAll();
//    }
//
//    @PatchMapping("name")
//    public Integer update(Long id, String name) {
//        return userService.update(id, name);
//    }
//
//    @PatchMapping("name2")
//    public Integer update2(Long id, String name) {
//        return userService.update(id, name);
//    }
}

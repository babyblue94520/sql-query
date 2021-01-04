package pers.clare.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import pers.clare.demo.data.entity.User;
import pers.clare.demo.service.UserService;

@RequestMapping("user")
@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    public Page<User> findAll(Pageable pageable) {
        return userService.findAll(pageable, null, null, null, null);
    }

    @PostMapping("name")
    public User add(User user) {
        return userService.insert(user);
    }
    @PatchMapping("name")
    public Integer modify(User user) {
        return userService.update(user);
    }
//
//    @PatchMapping("name2")
//    public Integer update2(Long id, String name) {
//        return userService.update(id, name);
//    }
}

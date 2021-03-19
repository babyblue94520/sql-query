package pers.clare.demo.controller;

import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.clare.core.sqlquery.page.Page;
import pers.clare.core.sqlquery.page.Pagination;
import pers.clare.demo.bo.Test2;
import pers.clare.demo.data.entity.TestUser;
import pers.clare.demo.data.sql.TestCrudRepository;
import pers.clare.demo.data.sql.TestRepository;
import pers.clare.demo.service.TestService;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@RequestMapping("test")
@RestController
public class TestController {
    @Autowired
    private TestService testService;

    @Autowired
    private TestCrudRepository testCrudRepository;
    @Autowired
    private TestRepository testRepository;

    @Autowired
    private DataSource dataSource;

    @GetMapping("page")
    public Page<TestUser> page(Pagination pagination) {
        return testCrudRepository.page(pagination);
    }

    @GetMapping("define")
    public Object define(Integer id) {
        return testRepository.define(id)+":"+testRepository.id();
    }

    @PostMapping("id")
    public Object find(Long id) {
        return testRepository.bbb(id);
    }

    @PostMapping(value = "aaa", consumes = "application/x-www-form-urlencoded")
    public Test2 add(Test2 test) {
        return test;
    }

    @PostMapping(value = "1")
    public TestUser add(@RequestBody TestUser testUser) {
        return testService.insert(testUser);
    }

    @PutMapping("1")
    public TestUser modify(TestUser testUser) {
        return testService.update(testUser);
    }

    @DeleteMapping("1")
    public int remove(TestUser testUser) {
        return testService.delete(testUser);
    }

    @PostMapping("2")
    public TestUser add2() {
        return testService.insert2(new TestUser(null, "test" + 1));
    }


    @PostMapping(value = "stack")
    public Page<TestUser> stack(@RequestBody TestUser testUser) {
         testService.insert(testUser);
         return testCrudRepository.page(Pagination.of(0,2000));
    }

}

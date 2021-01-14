package pers.clare.demo.controller;

import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pers.clare.demo.data.entity.Test;
import pers.clare.demo.data.repository.TestJpaRepository;
import pers.clare.demo.data.sql.TestCrudRepository;
import pers.clare.demo.data.sql.TestRepository;
import pers.clare.demo.service.TestService;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@RequestMapping("tx")
@RestController
public class TransactionController {
    @Autowired
    private TestCrudRepository testCrudRepository;
    @Autowired
    private TestService testService;

    @GetMapping("1")
    public Object test(String name) throws Exception {
        try{
            testService.transaction(new Test(null,name));
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println(testCrudRepository.count());
        System.out.println(testCrudRepository.find(1L));
        return testCrudRepository.findAll();
    }
}

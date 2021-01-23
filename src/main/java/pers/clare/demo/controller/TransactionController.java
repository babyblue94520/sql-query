package pers.clare.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.clare.demo.data.entity.TestUser;
import pers.clare.demo.data.sql.TestCrudRepository;
import pers.clare.demo.service.TestService;

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
            testService.transaction(new TestUser(null,name));
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println(testCrudRepository.count());
        System.out.println(testCrudRepository.findById(1L));
        return testCrudRepository.findAll();
    }
}

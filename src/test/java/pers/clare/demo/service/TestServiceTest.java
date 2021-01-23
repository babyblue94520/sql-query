package pers.clare.demo.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pers.clare.demo.data.entity.TestUser;
import pers.clare.demo.data.sql.TestCrudRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class TestServiceTest {
    @Autowired
    private TestService testService;

    @Autowired
    private TestCrudRepository testCrudRepository;

    @Test
    void insert() {
        TestUser testUser = new TestUser(null,"test");
        testService.insert(testUser);
        assertNotNull(testCrudRepository.find(testUser));
    }


    @Test
    void deleteAll() {
        testService.deleteAll();
        assertTrue(testCrudRepository.count()==0);
    }
}

package pers.clare.demo.data.sql;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pers.clare.demo.data.entity.TestUser;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TestCrudRepositoryTest {

    @Autowired
    private TestCrudRepository testCrudRepository;

    @Test
    @Order(1)
    void insert() {
        TestUser testUser = new TestUser(1L,"test");
        testCrudRepository.insert(testUser);
        assertNotNull(testCrudRepository.find(testUser));
    }


    @Test
    @Order(0)
    void deleteAll() {
        testCrudRepository.deleteAll();
        assertTrue(testCrudRepository.count()==0);
    }
}

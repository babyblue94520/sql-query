package pers.clare.demo.data.sql;

import lombok.extern.log4j.Log4j2;
import org.junit.Before;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pers.clare.CoreApplicationTest;
import pers.clare.demo.data.entity.TestUser;

import static org.junit.jupiter.api.Assertions.*;


@Log4j2
@SpringBootTest("spring.profiles.active=test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TestCrudRepositoryTest {

    @Autowired
    private TestCrudRepository testCrudRepository;


    @Test
    @Order(99)
    void deleteAll() {
        testCrudRepository.deleteAll();
        assertTrue(testCrudRepository.count() == 0);
    }

    @Test
    @Order(1)
    void test() {
        TestUser testUser = new TestUser(null, "test");
        testCrudRepository.insert(testUser);
        assertNotNull(testUser.getId());
        testUser = testCrudRepository.findById(testUser.getId());
        assertNotNull(testUser);

        testUser = new TestUser(testUser.getId(), "update");
        assertTrue(testCrudRepository.update(testUser) > 0);

        testUser = testCrudRepository.findById(testUser.getId());
        assertEquals("update", testUser.getName());
        assertTrue(testCrudRepository.delete(testUser) > 0);
    }

    @Test
    @Order(2)
    void insertAutoKey() {
        long id = 999L;
        TestUser testUser = new TestUser(id, "test1");
        testCrudRepository.insert(testUser);
        assertNotNull(testCrudRepository.findById(testUser.getId()));
    }


}

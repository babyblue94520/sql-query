package pers.clare.demo.data.sql;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pers.clare.demo.data.entity.User;

import static org.junit.jupiter.api.Assertions.*;


@Log4j2
@SpringBootTest("spring.profiles.active=test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TestCrudRepositoryTest {

    @Autowired
    private UserRepository userRepository;


    @Test
    @Order(99)
    void deleteAll() {
        userRepository.deleteAll();
        assertTrue(userRepository.count() == 0);
    }

    @Test
    @Order(1)
    void test() {
        User user = User.builder()
                .name("test")
                .build();
        userRepository.insert(user);
        assertNotNull(user.getId());
        user = userRepository.findById(user.getId());
        assertNotNull(user);

        user = User.builder()
                .id(user.getId())
                .name("test")
                .build();
        assertTrue(userRepository.update(user) > 0);

        user = userRepository.findById(user.getId());
        assertEquals("update", user.getName());
        assertTrue(userRepository.delete(user) > 0);
    }

    @Test
    @Order(2)
    void insertAutoKey() {
        long id = 999L;
        User user = User.builder()
                .id(id)
                .name("test1")
                .build();
        userRepository.insert(user);
        assertNotNull(userRepository.findById(user.getId()));
    }
}

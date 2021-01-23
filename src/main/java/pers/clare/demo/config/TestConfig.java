package pers.clare.demo.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import pers.clare.demo.data.entity.TestUser;
import pers.clare.demo.data.sql.TestCrudRepository;


@Log4j2
//@Configuration
public class TestConfig implements CommandLineRunner {
    @Autowired
    private TestCrudRepository testCrudRepository;

    @Override
    public void run(String... args) {
        if(testCrudRepository.count()==0){
            testCrudRepository.insert(new TestUser(null,"test"));
        }
    }
}

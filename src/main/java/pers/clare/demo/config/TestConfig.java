package pers.clare.demo.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import pers.clare.demo.data.entity.Test;
import pers.clare.demo.service.TestService;



@Log4j2
@Configuration
public class TestConfig implements CommandLineRunner {
    @Autowired
    private TestService testService;

    @Override
    public void run(String... args) {
        testService.insert(new Test(null,"test"));
    }
}

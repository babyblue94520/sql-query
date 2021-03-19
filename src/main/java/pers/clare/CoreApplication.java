package pers.clare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy(exposeProxy = true)
@SpringBootApplication
public class CoreApplication{
    public static void main(String[] args) {
        SpringApplication.run(CoreApplication.class, args);
    }
}

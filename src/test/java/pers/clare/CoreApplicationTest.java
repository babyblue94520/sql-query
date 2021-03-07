package pers.clare;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.springframework.boot.test.context.SpringBootTest;



@SpringBootTest("spring.profiles.active=test")
public class CoreApplicationTest extends Runner {

    @Override
    public Description getDescription() {
        return null;
    }

    @Override
    public void run(RunNotifier notifier) {

    }
}

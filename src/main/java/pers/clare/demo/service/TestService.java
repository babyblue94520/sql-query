package pers.clare.demo.service;

import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.clare.core.sqlquery.aop.SqlConnectionReuse;
import pers.clare.demo.data.entity.TestUser;
import pers.clare.demo.data.jpa.TestJpaRepository;
import pers.clare.demo.data.sql.TestCrudRepository;
import pers.clare.demo.data.sql.TestRepository;

import javax.transaction.Transactional;
import java.sql.Connection;
import java.util.Objects;

@Service
public class TestService {

    @Autowired
    private TestRepository testRepository;
    @Autowired
    private TestJpaRepository testJpaRepository;


    @Autowired
    private TestCrudRepository testCrudRepository;


    @SqlConnectionReuse
    public void reuse(int i) {
        Integer a = testRepository.define(i);
        Integer b = testRepository.id();
        if (!Objects.equals(a, b)) {
            System.out.println(a + ":" + b);
        }
    }

    @SqlConnectionReuse(transaction = true)
    public Object transaction(TestUser testUser) {
        System.out.println(testCrudRepository.count());
        testCrudRepository.insert(testUser);
        System.out.println(testCrudRepository.count());
        System.out.println(testCrudRepository.findById(1L));
        testCrudRepository.update(new TestUser(1L, "testaaa"));
        System.out.println(testCrudRepository.findById(1L));
        throw new IllegalArgumentException("test");
    }


    @SqlConnectionReuse(transaction = true)
    public TestUser insert(
            TestUser testUser
    ) {
        testCrudRepository.insert(testUser);
        System.out.println(proxy().find(testUser));
        System.out.println(proxy().findCommitted(testUser));
        testUser.setId(null);
        proxy().insert2(testUser);
        return testCrudRepository.find(testUser);
    }

    public TestUser update(
            TestUser testUser
    ) {
        testCrudRepository.update(testUser);
        return testCrudRepository.find(testUser);
    }

    public int delete(
            TestUser testUser
    ) {
        return testCrudRepository.delete(testUser);
    }

    public int deleteAll() {
        return testCrudRepository.deleteAll();
    }


    @SqlConnectionReuse(transaction = true)
    public TestUser insert2(
            TestUser testUser
    ) {
        TestUser testUser2 = testCrudRepository.insert(testUser);
//        return testUser2;
        throw new IllegalArgumentException("test");
    }

    @SqlConnectionReuse(isolation = Connection.TRANSACTION_READ_UNCOMMITTED)
    public TestUser find(TestUser testUser) {
        return testCrudRepository.find(testUser);
    }

    @SqlConnectionReuse(transaction = true,isolation = Connection.TRANSACTION_SERIALIZABLE)
    public TestUser findCommitted(TestUser testUser) {
        return testCrudRepository.find(testUser);
    }

    private TestService proxy() {
        return (TestService) AopContext.currentProxy();
    }
}

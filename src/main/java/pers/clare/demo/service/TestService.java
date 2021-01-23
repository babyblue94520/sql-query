package pers.clare.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.clare.core.sqlquery.aop.SqlConnectionReuse;
import pers.clare.demo.data.entity.TestUser;
import pers.clare.demo.data.repository.TestJpaRepository;
import pers.clare.demo.data.sql.TestCrudRepository;
import pers.clare.demo.data.sql.TestRepository;

@Service
public class TestService {

    @Autowired
    private TestRepository testRepository;
    @Autowired
    private TestJpaRepository testJpaRepository;


    @Autowired
    private TestCrudRepository testCrudRepository;


    @SqlConnectionReuse
    public void reuse(int i ){
        Integer a = testRepository.define(i);
        Integer b = testRepository.id();
//        if (!Objects.equals(a, b)) {
//            System.out.println(a + ":" + b);
//        }
    }

    @SqlConnectionReuse(transaction = true)
    public Object transaction(TestUser testUser){
        System.out.println(testCrudRepository.count());
        testCrudRepository.insert(testUser);
        System.out.println(testCrudRepository.count());
        System.out.println(testCrudRepository.findById(1L));
        testCrudRepository.update(new TestUser(1L,"testaaa"));
        System.out.println(testCrudRepository.findById(1L));
        throw new IllegalArgumentException("test");
    }

    public TestUser insert(
            TestUser testUser
    ) {
        testCrudRepository.insert(testUser);
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


    public TestUser insert2(
            TestUser testUser
    ) {
        TestUser testUser2 = testCrudRepository.insert(testUser);
        return testUser2;
    }
}

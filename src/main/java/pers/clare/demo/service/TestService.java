package pers.clare.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.clare.core.sqlquery.aop.SqlConnectionReuse;
import pers.clare.demo.data.entity.Test;
import pers.clare.demo.data.repository.TestJpaRepository;
import pers.clare.demo.data.sql.TestCrudRepository;
import pers.clare.demo.data.sql.TestRepository;

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
    public void reuse(int i ){
        Integer a = testRepository.define(i);
        Integer b = testRepository.id();
//        if (!Objects.equals(a, b)) {
//            System.out.println(a + ":" + b);
//        }
    }

    @SqlConnectionReuse(transaction = true)
    public Object transaction(Test test){
        System.out.println(testCrudRepository.count());
        testCrudRepository.insert(test);
        System.out.println(testCrudRepository.count());
        System.out.println(testCrudRepository.find(1L));
        testCrudRepository.update(new Test(1L,"testaaa"));
        System.out.println(testCrudRepository.find(1L));
        throw new IllegalArgumentException("test");
    }

    public Test insert(
            Test test
    ) {
        testJpaRepository.insert(test);
        return testCrudRepository.find(true, test);
    }

    public Test update(
            Test test
    ) {
        testCrudRepository.update(test);
        return testCrudRepository.find(true, test);
    }

    public int delete(
            Test test
    ) {
        return testCrudRepository.delete(test);
    }

    public Test insert2(
            Test test
    ) {
        Test test2 = testCrudRepository.insert(test);
        return test2;
    }
}

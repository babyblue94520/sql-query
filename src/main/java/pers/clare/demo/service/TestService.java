package pers.clare.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.clare.demo.data.entity.Test;
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

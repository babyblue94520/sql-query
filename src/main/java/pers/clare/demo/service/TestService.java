package pers.clare.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.clare.demo.data.SQLQueryConfig;
import pers.clare.demo.data.entity.Test;
import pers.clare.demo.data.repository.TestRepository;
import pers.clare.demo.data.sql.TestEntityRepository;

import javax.annotation.Resource;

@Service
public class TestService {
    @Autowired
    @Resource(name = SQLQueryConfig.SQLEntityServiceName)
    private SQLEntityService sqlEntityService;

    @Autowired
    private TestRepository testRepository;

    @Autowired
    private TestEntityRepository testEntityRepository;

    public Test findByName(
            String name
    ) {
        return testRepository.findByName(name);
    }

    public Test insert(
            Test test
    ) {
        testEntityRepository.insert(test);
        return testEntityRepository.find(true, test);
    }

    public Test update(
            Test test
    ) {
        testEntityRepository.update(test);
        return testEntityRepository.find(true, test);
    }

    public int delete(
            Test test
    ) {
        return testEntityRepository.delete(test);
    }

    public int delete2(
            Long id
    ) {
        return testEntityRepository.delete(id);
    }


    public Test insert2(
            Test test
    ) {
        Test test2 = testRepository.insert(test);
        return test2;
    }
}

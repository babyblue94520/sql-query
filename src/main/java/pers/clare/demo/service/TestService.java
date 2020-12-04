package pers.clare.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.clare.core.sqlquery.SQLEntityService;
import pers.clare.demo.data.SQLQueryConfig;
import pers.clare.demo.data.entity.Test;
import pers.clare.demo.data.entity.User;
import pers.clare.demo.data.repository.TestRepository;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

@Service
public class TestService {
    @Autowired
    @Resource(name = SQLQueryConfig.SQLEntityServiceName)
    private SQLEntityService sqlEntityService;

    @Autowired
    private TestRepository testRepository;
    public Test findByName(
            String name
    ) {
        return testRepository.findByName(name);
    }

    public Test insert(
            Test test
    ) {
        sqlEntityService.insert(test);
        return sqlEntityService.find(true, test);
    }

    public Test update(
            Test test
    ) {
        sqlEntityService.update(test);
        return sqlEntityService.find(true, test);
    }

    public int delete(
            Test test
    ) {
        return sqlEntityService.delete(test);
    }

    public int delete2(
            Long id
    ) {
        return sqlEntityService.delete(Test.class, id);
    }


    public Test insert2(
            Test test
    ) {
        Test test2 = testRepository.insert(test);
        return test2;
    }
}

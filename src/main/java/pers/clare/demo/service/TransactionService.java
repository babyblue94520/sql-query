package pers.clare.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.clare.core.sqlquery.aop.SqlConnectionReuse;
import pers.clare.demo.data.sql.TransactionRepository;
import pers.clare.demo.data.sql.UserRepository;

@Service
public class TransactionService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    /**
     * use the some connection in different methods
     */
    @SqlConnectionReuse
    public String queryDefineValue(Long id, String name) {
        transactionRepository.updateName(id, name);
        return String.format("old name:%s , new name:%s", transactionRepository.getOldName(), name);
    }
}

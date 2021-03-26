package pers.clare.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.clare.core.sqlquery.aop.SqlConnectionReuse;
import pers.clare.demo.data.sql.TransactionRepository;
import pers.clare.demo.data.sql.UserRepository;

import java.sql.Connection;

@Service
public class ConnectionReuseService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;


    @SqlConnectionReuse(transaction = true,isolation = Connection.TRANSACTION_READ_UNCOMMITTED)
    public void transaction(StringBuffer result, Long id, String name, int count) {
        multiUpdate(result, id, name, count);
    }

    public void non(StringBuffer result, Long id, String name, int count) {
        multiUpdate(result, id, name, count);
    }

    private void multiUpdate(StringBuffer result, Long id, String name, int count) {
        for (int i = 0; i < count; i++) {
            StringBuilder sb = new StringBuilder();
            sb.append("-------------------------------------").append('\n');
            sb.append(Thread.currentThread().getName() + " start").append('\n');
            sb.append(queryDefineValue(id, name + System.currentTimeMillis())).append('\n');
            sb.append(Thread.currentThread().getName() + " end").append('\n');
            if (count > 1) {
                sb.append(Thread.currentThread().getName() + " sleep").append('\n');
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            result.append(sb);
        }
    }

    /**
     * use the some connection in different methods
     */
    @SqlConnectionReuse
    public String queryDefineValue(Long id, String name) {
        transactionRepository.updateName(id, name);
        return String.format("old name:%s , new name:%s", transactionRepository.getOldName(), name);
    }
}

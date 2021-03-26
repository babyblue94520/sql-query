package pers.clare.demo.service;

import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.clare.core.sqlquery.aop.SqlConnectionReuse;
import pers.clare.demo.data.entity.User;
import pers.clare.demo.data.sql.TransactionRepository;
import pers.clare.demo.data.sql.UserRepository;

import java.sql.Connection;

@Service
public class ConnectionReuseService {
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

    @SqlConnectionReuse(transaction = true, isolation = Connection.TRANSACTION_READ_UNCOMMITTED)
    public void transaction(StringBuffer result, Long id, String name, int count) {
        multiUpdate(result, id, name, count);
    }

    public void non(StringBuffer result, Long id, String name, int count) {
        multiUpdate(result, id, name, count);
    }

    public String rollback(Long id, String name) {
        StringBuilder sb = new StringBuilder();
        try {
            proxy().updateException(sb, id, name);
        } catch (Exception e) {
            sb.append(e.getMessage()).append('\n');
        }
        sb.append(userRepository.findById(id)).append('\n');
        return sb.toString();
    }

    @SqlConnectionReuse(transaction = true)
    public void updateException(StringBuilder sb, Long id, String name) {
        String result = queryDefineValue(id, name);
        sb.append(result).append('\n');
        sb.append("------some connection------").append('\n');
        User user = userRepository.findById(id);
        sb.append(user).append('\n');

        result = queryDefineValue(id, name+2);
        sb.append(result).append('\n');

        sb.append("------uncommitted------").append('\n');
        user = proxy().findByIdUncommitted(id);
        sb.append(user).append('\n');

        sb.append("------committed------").append('\n');
        user = proxy().findById(id);
        sb.append(user).append('\n');
        throw new RuntimeException("rollback");
    }

    @SqlConnectionReuse
    public User findById(Long id) {
        return userRepository.findById(id);
    }

    @SqlConnectionReuse(isolation = Connection.TRANSACTION_READ_UNCOMMITTED)
    public User findByIdUncommitted(Long id) {
        return userRepository.findById(id);
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

    private ConnectionReuseService proxy() {
        return (ConnectionReuseService) AopContext.currentProxy();
    }

}

package pers.clare.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.clare.demo.data.sql.TransactionRepository;
import pers.clare.demo.service.TransactionService;

@RequestMapping("transaction")
@RestController
public class TransactionController {

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private TransactionService transactionService;

    @GetMapping("reuse")
    public String reuse(long id, String name) {
        return transactionService.queryDefineValue(id, name);
    }
}

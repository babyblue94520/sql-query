package pers.clare.demo.controller;

import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pers.clare.demo.data.sql.TransactionRepository;
import pers.clare.demo.service.ConnectionReuseService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@RequestMapping("reuse")
@RestController
public class ConnectionReuseController {

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private ConnectionReuseService connectionReuseService;

    @GetMapping
    public String reuse(long id, String name) {
        return connectionReuseService.queryDefineValue(id, name);
    }

    @GetMapping("transaction")
    public String transaction(
            @ApiParam(value = "執行緒數量", example = "3")
            @RequestParam(required = false, defaultValue = "3") int thread
            ,
            @ApiParam(value = "修改次數", example = "2")
            @RequestParam(required = false, defaultValue = "2") int updateCount
            , long id
            , String name
    ) throws ExecutionException, InterruptedException {
        StringBuffer sb = new StringBuffer();
        run(thread, () -> {
            connectionReuseService.transaction(sb, id, name, updateCount);
            return null;
        });
        return sb.toString();
    }

    @GetMapping("transaction/non")
    public String non(
            @ApiParam(value = "執行緒數量", example = "3")
            @RequestParam(required = false, defaultValue = "3") int thread
            ,
            @ApiParam(value = "修改次數", example = "2")
            @RequestParam(required = false, defaultValue = "2") int updateCount
            , long id
            , String name
    ) throws ExecutionException, InterruptedException {
        StringBuffer sb = new StringBuffer();
        run(thread, () -> {
            connectionReuseService.non(sb, id, name, updateCount);
            return null;
        });
        return sb.toString();
    }

    @GetMapping("transaction/rollback")
    public String rollback(
            long id
            , String name
    ) {
        return connectionReuseService.rollback(id, name);
    }

    private void run(int thread, Callable<Object> task) throws InterruptedException, ExecutionException {
        ExecutorService executors = Executors.newFixedThreadPool(thread);
        List<Callable<Object>> tasks = new ArrayList<>();
        for (int t = 0; t < thread; t++) {
            tasks.add(task);
        }
        List<Future<Object>> futures = executors.invokeAll(tasks);
        for (Future<Object> f : futures) {
            f.get();
        }
        executors.shutdown();
    }
}

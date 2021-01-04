package pers.clare.demo.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import pers.clare.demo.bo.Test2;
import pers.clare.demo.data.entity.Test;
import pers.clare.demo.data.entity.User;
import pers.clare.demo.data.sql.TestEntityRepository;
import pers.clare.demo.service.TestService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@RequestMapping("test")
@RestController
public class TestController {
    @Autowired
    private TestService testService;

    @Autowired
    private TestEntityRepository testEntityRepository;

    @PostMapping("id")
    public Object find(Long id) {
        return testEntityRepository.bbb(id);
    }

    @PostMapping(value = "aaa",consumes = "application/x-www-form-urlencoded")
    public Test2 add( Test2 test) {
        return test;
    }

    @PostMapping(value = "1")
    public Test add(@RequestBody Test test) {
        return testService.insert(test);
    }

    @PutMapping("1")
    public Test modify(Test test) {
        return testService.update(test);
    }

    @DeleteMapping("1")
    public int remove(Test test) {
        return testService.delete(test);
    }

    @PostMapping("2")
    public Test add2() {
        return testService.insert2(new Test(null, "test" + 1));
    }

    @GetMapping("1")
    public String test(
            @ApiParam(value = "執行緒數量", example = "8")
            @RequestParam(required = false, defaultValue = "8") final int thread
            , @ApiParam(value = "數量", example = "100")
            @RequestParam(required = false, defaultValue = "100") final int max
    ) throws Exception {
        ExecutorService executors = Executors.newFixedThreadPool(thread);
        StringBuilder sp = new StringBuilder();
        long start = System.currentTimeMillis();
        List<Callable<Integer>> tasks = new ArrayList<>();
        for (int t = 0; t < thread; t++) {
            tasks.add(() -> {
                for (int i = 0; i < max; i++) {
                    testService.insert(new Test(null, "test" + i));
                }
                return max;
            });
        }
        long total = 0;
        List<Future<Integer>> futures = executors.invokeAll(tasks);
        for (Future<Integer> f : futures) {
            total += f.get();
        }
        long ms = System.currentTimeMillis() - start;
        sp.append("total time:" + ms + '\n');
        sp.append("average time:" + (total * 1000 / ms) + '\n');
        executors.shutdown();
        return sp.toString();
    }

    @GetMapping("2")
    public String test2(
            @ApiParam(value = "執行緒數量", example = "8")
            @RequestParam(required = false, defaultValue = "8") final int thread
            , @ApiParam(value = "數量", example = "100")
            @RequestParam(required = false, defaultValue = "100") final int max
    ) throws Exception {
        ExecutorService executors = Executors.newFixedThreadPool(thread);
        StringBuilder sp = new StringBuilder();
        long start = System.currentTimeMillis();
        List<Callable<Integer>> tasks = new ArrayList<>();
        for (int t = 0; t < thread; t++) {
            tasks.add(() -> {
                for (int i = 0; i < max; i++) {
                    testService.insert2(new Test(null, "test" + i));
                }
                return max;
            });
        }
        long total = 0;
        List<Future<Integer>> futures = executors.invokeAll(tasks);
        for (Future<Integer> f : futures) {
            total += f.get();
        }
        long ms = System.currentTimeMillis() - start;
        sp.append("total time:" + ms + '\n');
        sp.append("average time:" + (total * 1000 / ms) + '\n');
        executors.shutdown();
        return sp.toString();
    }
}

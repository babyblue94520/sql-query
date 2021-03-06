package pers.clare.demo.controller;

import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import pers.clare.core.sqlquery.page.Pagination;
import pers.clare.demo.data.entity.User;
import pers.clare.demo.data.jpa.UserJpaRepository;
import pers.clare.demo.data.sql.UserQueryRepository;
import pers.clare.demo.data.sql.UserRepository;
import pers.clare.demo.service.UserService;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Consumer;

@RequestMapping("per")
@RestController
public class PerformanceController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserQueryRepository userQueryRepository;
    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private UserService userService;

    @GetMapping("jpa/page")
    public String jpaPage(
            @ApiParam(value = "執行緒數量", example = "8")
            @RequestParam(required = false, defaultValue = "8") final int thread
            , @ApiParam(value = "數量", example = "100")
            @RequestParam(required = false, defaultValue = "100") final int max
    ) throws Exception {
        return run(thread, max, (i) -> userJpaRepository.findAll(PageRequest.of(0,1)));
    }

    @GetMapping("jpa/insert")
    public String jpaInsert(
            @ApiParam(value = "執行緒數量", example = "8")
            @RequestParam(required = false, defaultValue = "8") final int thread
            , @ApiParam(value = "數量", example = "100")
            @RequestParam(required = false, defaultValue = "100") final int max
    ) throws Exception {
        return run(thread, max, (i) -> userJpaRepository.save(User.builder()
                .account(Thread.currentThread().getName() + i)
                .name(Thread.currentThread().getName())
                .build()
        ));
    }

    @GetMapping("sql/page")
    public String page(
            @ApiParam(value = "執行緒數量", example = "8")
            @RequestParam(required = false, defaultValue = "8") final int thread
            , @ApiParam(value = "數量", example = "100")
            @RequestParam(required = false, defaultValue = "100") final int max
    ) throws Exception {
        return run(thread, max, (i) -> userQueryRepository.findAll(Pagination.of(0, 1)));
    }

    @GetMapping("sql/page/map")
    public String pageMap(
            @ApiParam(value = "執行緒數量", example = "8")
            @RequestParam(required = false, defaultValue = "8") final int thread
            , @ApiParam(value = "數量", example = "100")
            @RequestParam(required = false, defaultValue = "100") final int max
    ) throws Exception {
        return run(thread, max, (i) -> userQueryRepository.findAllMap(Pagination.of(0, 1)));
    }

    @GetMapping("sql/insert")
    public String insert(
            @ApiParam(value = "執行緒數量", example = "8")
            @RequestParam(required = false, defaultValue = "8") final int thread
            , @ApiParam(value = "數量", example = "100")
            @RequestParam(required = false, defaultValue = "100") final int max
    ) throws Exception {
        return run(thread, max, (i) -> userService.insert(User.builder()
                .account(Thread.currentThread().getName() + i)
                .name(Thread.currentThread().getName())
                .build()
        ));
    }

    private String run(int thread, int max, Consumer<Integer> runnable) throws InterruptedException, ExecutionException {
        ExecutorService executors = Executors.newFixedThreadPool(thread);
        StringBuilder sp = new StringBuilder();
        long start = System.currentTimeMillis();
        List<Callable<Integer>> tasks = new ArrayList<>();
        for (int t = 0; t < thread; t++) {
            tasks.add(() -> {
                for (int i = 0; i < max; i++) {
                    runnable.accept(i);
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

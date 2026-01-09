package com.okayjam.web.controller;

import com.okayjam.web.lock.service.LockService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Chen weiguang chen2621978@gmail.com
 * @date 2021/08/16 16:47
 **/
@RequestMapping("api/test")
@RestController
public class TestController {

    @Autowired
    @Qualifier("dbLockService")
    LockService lockService;


    //    @Operation(summary = "ping简单测试", description = "测试基本服务是否正常")
    @RequestMapping("/ping")
    public String ping(@RequestHeader Map<String, String> headers) {
        return "pong";
    }


    @GetMapping("/check-vt")
    public Map<String, Object> checkVirtualThread() {
        Thread currentThread = Thread.currentThread();
        // JDK 21 引入了 isVirtual() 方法
        boolean isVirtual = currentThread.isVirtual();

        return Map.of(
                "isVirtual", isVirtual,
                "threadName", currentThread.getName(),
                "threadClass", currentThread.getClass().getName(),
                "message", isVirtual ? "虚拟线程已启用" : "当前使用的是平台线程(传统线程)"
        );
    }

    @GetMapping("/blocking")
    public String blockingTest() throws InterruptedException {
        Thread currentThread = Thread.currentThread();
        String threadInfo = String.format(
                "线程: %s, 虚拟线程: %s",
                currentThread.getName(),
                currentThread.isVirtual()
        );

        // 模拟 I/O 阻塞操作（如数据库查询、HTTP 调用）
        Thread.sleep(1000);

        return threadInfo;
    }

    /**
     * 慢接口 - 用于外部压测工具测试（ab, wrk, hey等）
     * 使用方法：
     * 1. 设置 spring.threads.virtual.enabled=false，重启，执行: ab -n 500 -c 100 http://localhost:8080/api/test/slow
     * 2. 设置 spring.threads.virtual.enabled=true，重启，执行: ab -n 500 -c 100 http://localhost:8080/api/test/slow
     * 对比两次的 Requests per second 和 Time per request
     */
    @GetMapping("/slow")
    public Map<String, Object> slowRequest(@RequestParam(defaultValue = "500") int sleepMs) throws InterruptedException {
        Thread currentThread = Thread.currentThread();
        long start = System.currentTimeMillis();
        
        // 模拟 I/O 阻塞
        Thread.sleep(sleepMs);
        
        Map<String, Object> result = new HashMap<>();
        result.put("threadName", currentThread.getName());
        result.put("isVirtual", currentThread.isVirtual());
        result.put("sleepMs", sleepMs);
        result.put("actualMs", System.currentTimeMillis() - start);
        result.put("activeThreads", Thread.activeCount());
        return result;
    }

    /**
     * 直接在代码中对比虚拟线程和平台线程性能
     * 访问: http://localhost:8080/api/test/compare?tasks=1000&sleepMs=100
     * 
     * @param tasks 任务数量（默认1000）
     * @param sleepMs 每个任务睡眠时间（默认100ms）
     * @param poolSize 平台线程池大小（默认200）
     */
    @GetMapping("/compare")
    public Map<String, Object> compareThreads(
            @RequestParam(defaultValue = "1000") int tasks,
            @RequestParam(defaultValue = "100") int sleepMs,
            @RequestParam(defaultValue = "200") int poolSize) throws InterruptedException {
        
        Map<String, Object> result = new HashMap<>();
        result.put("taskCount", tasks);
        result.put("sleepMsPerTask", sleepMs);
        result.put("platformThreadPoolSize", poolSize);
        
        // 任务定义：模拟 I/O 阻塞
        Runnable task = () -> {
            try {
                Thread.sleep(sleepMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        // ========== 测试虚拟线程 ==========
        AtomicInteger virtualMaxThreads = new AtomicInteger(0);
        long startVirtual = System.currentTimeMillis();
        CountDownLatch virtualLatch = new CountDownLatch(tasks);
        
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < tasks; i++) {
                executor.submit(() -> {
                    try {
                        int current = Thread.activeCount();
                        virtualMaxThreads.updateAndGet(max -> Math.max(max, current));
                        task.run();
                    } finally {
                        virtualLatch.countDown();
                    }
                });
            }
            virtualLatch.await();
        }
        long virtualTime = System.currentTimeMillis() - startVirtual;

        // ========== 测试平台线程 ==========
        AtomicInteger platformMaxThreads = new AtomicInteger(0);
        long startPlatform = System.currentTimeMillis();
        CountDownLatch platformLatch = new CountDownLatch(tasks);
        
        try (ExecutorService executor = Executors.newFixedThreadPool(poolSize)) {
            for (int i = 0; i < tasks; i++) {
                executor.submit(() -> {
                    try {
                        int current = Thread.activeCount();
                        platformMaxThreads.updateAndGet(max -> Math.max(max, current));
                        task.run();
                    } finally {
                        platformLatch.countDown();
                    }
                });
            }
            platformLatch.await();
        }
        long platformTime = System.currentTimeMillis() - startPlatform;

        // ========== 结果对比 ==========
        Map<String, Object> virtualResult = new HashMap<>();
        virtualResult.put("耗时(ms)", virtualTime);
        virtualResult.put("理论最短耗时(ms)", sleepMs);
        virtualResult.put("吞吐量(tasks/sec)", tasks * 1000.0 / virtualTime);
        
        Map<String, Object> platformResult = new HashMap<>();
        platformResult.put("耗时(ms)", platformTime);
        platformResult.put("理论最短耗时(ms)", (long) Math.ceil((double) tasks / poolSize) * sleepMs);
        platformResult.put("吞吐量(tasks/sec)", tasks * 1000.0 / platformTime);
        
        result.put("虚拟线程", virtualResult);
        result.put("平台线程", platformResult);
        result.put("虚拟线程快了", String.format("%.2f 倍", (double) platformTime / virtualTime));
        result.put("结论", platformTime > virtualTime ? "✅ 虚拟线程更快！" : "⚠️ 平台线程更快（可能任务数太少或I/O时间太短）");
        
        return result;
    }

    @RequestMapping("/test1")
    public String demo() {
        String key = "jam";
        if (!lockService.tryLock(key)) {
            // 没有获取到锁返回
            return "";
        }
        try {
            // 这里写业务逻辑
            Thread.sleep(1000);
            System.out.println(Thread.currentThread().getStackTrace()[1].getMethodName());
            Thread.sleep(2 * 1000);
        } catch (Exception e) {
        } finally {
            lockService.releaseLock(key);
        }

        return "Hello！";
    }
}
package com.okayjam.web.controller;

import com.okayjam.web.lock.service.LockService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
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

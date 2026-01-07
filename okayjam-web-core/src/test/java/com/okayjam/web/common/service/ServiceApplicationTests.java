package com.okayjam.web.common.service;

import com.okayjam.web.lock.service.LockService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ServiceApplicationTests {

    @Resource("dbLockService")
    LockService lockService;

    @Test
    void tryLock() throws InterruptedException {
        String key = "jam";
        if (!lockService.tryLock(key)) {
            // 没有获取到锁返回
            return;
        }
        try {
            // 这里写业务逻辑
            Thread.sleep(1000);
            System.out.println(Thread.currentThread() .getStackTrace()[1].getMethodName());
            Thread.sleep(2* 1000);
        } catch (Exception e) {
        } finally {
            lockService.releaseLock(key);
        }
    }
}

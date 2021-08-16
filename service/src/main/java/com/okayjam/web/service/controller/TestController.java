package com.okayjam.web.service.controller;

import com.okayjam.web.lock.service.LockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Chen weiguang chen2621978@gmail.com
 * @date 2021/08/16 16:47
 **/
@RestController
public class TestController {

    @Autowired
    @Qualifier("dbLockService")
    LockService lockService;

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
            System.out.println(Thread.currentThread() .getStackTrace()[1].getMethodName());
            Thread.sleep(2* 1000);
        } catch (Exception e) {
        } finally {
            lockService.releaseLock(key);
        }

        return "Hello！";
    }
}

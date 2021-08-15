package com.okayjam.web.lock.service;

public interface LockService {
        /**
         * 尝试获得锁，成功为true，失败为false
         */
        public boolean tryLock(String key);

        /**
         * 如果已经有其他程序占用该锁，并且超过timeoutMs（毫秒）时间，就强制清除这个锁占用
         * 即根据key先删除记录，再添加记录
         */
        public boolean tryLockWithClear(String key, Long timeoutMs);

        /**
         * 释放锁，根据key删除记录
         */
        public void releaseLock(String key);

}

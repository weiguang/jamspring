package com.okayjam.web.lock.service.impl;

import com.okayjam.web.lock.entity.BaseDistributedLock;
import com.okayjam.web.lock.mapper.BaseDistributedLockMapper;
import com.okayjam.web.lock.service.LockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DbLockService implements LockService {
    private Logger logger = LoggerFactory.getLogger(DbLockService.class);

    @Autowired
    BaseDistributedLockMapper lockMapper;

    private BaseDistributedLock tryLockInternal(String key) {
        BaseDistributedLock insert = new BaseDistributedLock();
        insert.setLockKey(key);
        // 注意的地方1
        int count = lockMapper.insertKey(insert);
        if (count == 0) {
            BaseDistributedLock ready = lockMapper.queryByKey(key);
            logger.warn("can not lock the key: {}, {}, {}", ready.getLockKey(), ready.getCreateTime(),
                    ready.getId());
            return ready;
        }
        logger.info("yes got the lock by key: {}, id:{}", key, insert.getId());
        return null;
    }

    /** 超时清除锁占用，并重新加锁 **/
    @Override
    public boolean tryLockWithClear(String key, Long timeoutMs) {
        BaseDistributedLock lock = tryLockInternal( key);
        if (lock == null) return true;
        if (System.currentTimeMillis() - lock.getCreateTime().getTime() <= timeoutMs) {
            logger.warn("sorry, can not get the key. : {}, {}, {}", key, lock.getId(), lock.getCreateTime());
            return false;
        }
        logger.warn("the key already timeout within : {}, {}, will clear", key, timeoutMs);
        // 注意的地方2,通过id删除避免释放错了
        int count = lockMapper.deleteById(lock.getId());
        if (count == 0) {
            logger.warn("sorry, the key already preemptive by others: {}, {}", lock.getId(), lock.getLockKey());
            return false;
        }
        lock = tryLockInternal(key);
        return lock == null;
    }


    @Override
    public boolean tryLock(String key) {
        return tryLockInternal(key) == null;
    }


    @Override
    public void releaseLock(String key) {
        lockMapper.deleteByKey(key);
    }
}

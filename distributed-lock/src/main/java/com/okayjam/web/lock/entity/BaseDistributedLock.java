package com.okayjam.web.lock.entity;

import java.util.Date;
import java.io.Serializable;

/**
 * (BaseDistributedLock)实体类
 *
 * @author makejava
 * @since 2021-08-15 19:09:06
 */
public class BaseDistributedLock implements Serializable {
    private static final long serialVersionUID = 412516477666048517L;
    
    private Long id;
    //锁名称
    private String lockKey;
    //创建时间
    private Date createTime;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLockKey() {
        return lockKey;
    }

    public void setLockKey(String lockKey) {
        this.lockKey = lockKey;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

}
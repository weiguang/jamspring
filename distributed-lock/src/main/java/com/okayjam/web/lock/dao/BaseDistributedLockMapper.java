package com.okayjam.web.lock.dao;

import com.okayjam.web.lock.entity.BaseDistributedLock;
import org.apache.ibatis.annotations.Mapper;

/**
 * (BaseDistributedLock)表数据库访问层
 *
 * @author makejava
 * @since 2021-08-15 16:48:23
 */
@Mapper
public interface BaseDistributedLockMapper {

    /**
     * 通过key查询单条数据
     *
     * @param key 主键
     * @return 实例对象
     */
    BaseDistributedLock queryByKey(String key);


    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Long id);

    /**
     * 通过key删除数据
     *
     * @param key key
     * @return 影响行数
     */
    int deleteByKey(String key);


    /**
     * 新增数据
     *
     * @param lock 实例对象
     * @return 影响行数
     */
    int insertKey(BaseDistributedLock lock);

}
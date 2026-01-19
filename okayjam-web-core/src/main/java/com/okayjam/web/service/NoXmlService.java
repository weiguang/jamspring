package com.okayjam.web.service;

import com.okayjam.web.dao.NoXmlDao;
import com.okayjam.web.entity.Demo;

import java.util.List;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @description: ${description}
 * @author: Chen wei guang
 * @create: 2018/08/08 14:43
 **/
@Slf4j
@Service
public class NoXmlService {

    @Resource
    private NoXmlDao noXmlDao;

    public boolean insert(Demo model) {
        return noXmlDao.insert(model) > 0;
    }

    public Demo select(int id) {
        return noXmlDao.select(id);
    }

    public List<Demo> selectAll() {
        return noXmlDao.selectAll();
    }

    public boolean updateValue(Demo model) {
        return noXmlDao.updateValue(model) > 0;
    }

    public boolean delete(Integer id) {
        return noXmlDao.delete(id) > 0;
    }

    @Async
    public void testAsync() {
        log.info("test start");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.info("test end");
    }
}

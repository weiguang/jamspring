package com.okayjam.web.service.service;

import com.okayjam.web.service.mapper.DemoMapper;
import com.okayjam.web.service.entity.Demo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description: ${description}
 * @author: Chen wei guang
 * @create: 2018/08/08 14:43
 **/
@Service
public class DemoService {

    private final DemoMapper dao;

    @Autowired
    public DemoService(DemoMapper dao) {
        this.dao = dao;
    }

    public boolean insert(Demo model) {
        return dao.insert(model) > 0;
    }

    public Demo select(int id) {
        return dao.select(id);
    }

    public List<Demo> selectAll() {
        return dao.selectAll();
    }

    public boolean updateValue(Demo model) {
        return dao.updateValue(model) > 0;
    }

    public boolean delete(Integer id) {
        return dao.delete(id) > 0;
    }
}

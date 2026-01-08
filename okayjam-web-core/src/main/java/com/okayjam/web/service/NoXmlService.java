package com.okayjam.web.service;

import com.okayjam.web.dao.NoXmlMapper;
import com.okayjam.web.entity.Demo;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @description: ${description}
 * @author: Chen wei guang
 * @create: 2018/08/08 14:43
 **/
@Service
public class NoXmlService {

    private final NoXmlMapper dao;

    @Autowired
    public NoXmlService(NoXmlMapper dao) {
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

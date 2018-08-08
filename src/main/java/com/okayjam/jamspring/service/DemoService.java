package com.okayjam.jamspring.service;

import com.okayjam.jamspring.mapper.DemoMapper;
import com.okayjam.jamspring.model.DemoModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description: ${description}
 * @author: Chen wei guang <weiguangchen@sf-express.com>
 * @create: 2018/08/08 14:43
 **/
@Service
public class DemoService {

    private final DemoMapper dao;

    @Autowired
    public DemoService(DemoMapper dao) {
        this.dao = dao;
    }

    public boolean insert(DemoModel model) {
        return dao.insert(model) > 0;
    }

    public DemoModel select(int id) {
        return dao.select(id);
    }

    public List<DemoModel> selectAll() {
        return dao.selectAll();
    }

    public boolean updateValue(DemoModel model) {
        return dao.updateValue(model) > 0;
    }

    public boolean delete(Integer id) {
        return dao.delete(id) > 0;
    }
}

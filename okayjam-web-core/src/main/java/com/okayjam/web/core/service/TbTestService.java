package com.okayjam.web.core.service;

import com.okayjam.web.core.entity.TbTest;
import com.okayjam.web.core.dao.TbTestMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description: ${description}
 * @author: Chen wei guang
 * @create: 2018/08/08 14:43
 **/
@Service
public class TbTestService {

    @Autowired
    private  TbTestMapper dao;

    public boolean insert(TbTest model) {
        return dao.insert(model) > 0;
    }

    public TbTest select(int id) {
        return dao.selectByPrimaryKey(id);
    }

    public List<TbTest> selectAll() {
        return dao.selectAll();
    }

    public boolean updateValue(TbTest model) {
        return dao.updateByPrimaryKey(model) > 0;
    }

    public boolean delete(Integer id) {
        return dao.deleteByPrimaryKey(id) > 0;
    }
}

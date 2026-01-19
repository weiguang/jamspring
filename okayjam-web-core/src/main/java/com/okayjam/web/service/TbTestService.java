package com.okayjam.web.service;

import com.okayjam.web.dao.TbTestDao;
import com.okayjam.web.entity.TbTest;

import java.util.List;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * @description: ${description}
 * @author: Chen wei guang
 * @create: 2018/08/08 14:43
 **/
@Service
public class TbTestService {

    @Resource
    private TbTestDao tbTestDao;

    public boolean insert(TbTest model) {
        return tbTestDao.insert(model) > 0;
    }

    public TbTest select(Long id) {
        return tbTestDao.queryById(id);
    }

    public List<TbTest> selectAll() {
        return tbTestDao.queryAll(new TbTest());
    }

    public boolean updateValue(TbTest model) {
        return tbTestDao.update(model) > 0;
    }

    public boolean delete(Long id) {
        return tbTestDao.deleteById(id) > 0;
    }
}

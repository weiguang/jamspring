package com.okayjam.web.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.okayjam.web.common.dto.ResponseDto;
import com.okayjam.web.common.util.DateUtil;
import com.okayjam.web.entity.TbTest;
import com.okayjam.web.req.TbTestQueryReq;
import com.okayjam.web.service.TbTestService;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description: ${description}
 * @author: Chen wei guang
 * @create: 2018/08/08 14:43
 **/
@RestController
@RequestMapping("api/dbTest")
public class TbTestController {

    @Resource
    private TbTestService service;

    /**
     * 接口测试
     *
     * @return JSON 字符串
     */
    @RequestMapping("all")
    public List<TbTest> allTbTest() {
        return service.selectAll();
    }

    /**
     * 接口测试 - GET请求
     *
     * @param req 查询请求参数，包含分页参数和key过滤条件
     * @return JSON 字符串
     */
    @GetMapping("list")
    public ResponseDto<?> listGet(@ModelAttribute TbTestQueryReq req) {
        return listInternal(req);
    }

    /**
     * 接口测试 - POST请求
     *
     * @param req 查询请求参数，包含分页参数和key过滤条件
     * @return JSON 字符串
     */
    @PostMapping("list")
    public ResponseDto<?> list(@RequestBody TbTestQueryReq req) {
        return listInternal(req);
    }

    /**
     * 内部查询方法
     *
     * @param req 查询请求参数
     * @return JSON 字符串
     */
    private ResponseDto<?> listInternal(TbTestQueryReq req) {
        try (Page<Object> page = PageHelper.startPage(req.getPageNum(), req.getPageSize())) {
            List<TbTest> list = service.selectAll();
            return ResponseDto.success(new PageInfo<>(list));
        }
    }


    /**
     * 接口测试
     *
     * @return JSON 字符串
     */
    @RequestMapping("insert")
    public Integer insert() {
        TbTest tbTest = new TbTest();
        tbTest.setValue("date");
        tbTest.setKey(DateUtil.formatDate(LocalDateTime.now()));
        service.insert(tbTest);
        return tbTest.getId();
    }

}


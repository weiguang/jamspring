package com.okayjam.web.service.controller;

import com.okayjam.web.service.entity.TbTest;
import com.okayjam.web.service.service.TbTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * @description: ${description}
 * @author: Chen wei guang
 * @create: 2018/08/08 14:43
 **/
@Controller
public class TbTestController {

    private final TbTestService service;

    @Autowired
    public TbTestController(TbTestService service) {
        this.service = service;
    }

    /**
     * JSP 测试
     * @return JSP 视图
     */
    @RequestMapping("/TbTest")
    public ModelAndView TbTest() {
        ModelAndView mv = new ModelAndView("hello"); // 对应 TbTest.jsp 路径
        mv.addObject("value", "测试值");
        return mv;
    }

    /**
     * 接口测试
     * @return JSON 字符串
     */
        @RequestMapping("/TbTests")
    @ResponseBody
    public List<TbTest> allTbTest() {
        return service.selectAll();
    }
    // @ResponseBody 如果返回的是对象 会自动转为json字符串，如果返回的是String 则返回该字符串
}


package com.okayjam.jamspring.controller;

import com.okayjam.jamspring.model.DemoModel;
import com.okayjam.jamspring.service.DemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * @description: ${description}
 * @author: Chen wei guang <weiguangchen@sf-express.com>
 * @create: 2018/08/08 14:43
 **/
@Controller
public class DemoController {

    private final DemoService service;

    @Autowired
    public DemoController(DemoService service) {
        this.service = service;
    }

    /**
     * JSP 测试
     * @return JSP 视图
     */
    @GetMapping("/hello")
    public ModelAndView demo() {
        ModelAndView mv = new ModelAndView("hello"); // 对应 demo.jsp 路径
        mv.addObject("value", "测试值");
        return mv;
    }

    /**
     * 接口测试
     * @return JSON 字符串
     */
    @GetMapping("/demos")
    @ResponseBody
    public List<DemoModel> allDemo() {
        return service.selectAll();
    }
    // @ResponseBody 如果返回的是对象 会自动转为json字符串，如果返回的是String 则返回该字符串
}


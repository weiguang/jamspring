package com.okayjam.jamspring.controller;

import com.okayjam.jamspring.entity.Demo;
import com.okayjam.jamspring.service.DemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

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
    @RequestMapping("/hello")
    public ModelAndView demo() {
        ModelAndView mv = new ModelAndView("hello"); // 对应 demo.jsp 路径
        mv.addObject("value", "测试值");
        return mv;
    }

    @RequestMapping("/tep")
    public ModelAndView tep(String name) {
        ModelAndView mv = new ModelAndView("hello"); // 对应 demo.jsp 路径
        mv.addObject("value", name);
        return mv;
    }

    @RequestMapping("/tep2")
    public ModelAndView tep2(@RequestParam(value="name") String username) {
        ModelAndView mv = new ModelAndView("hello"); // 对应 demo.jsp 路径
        mv.addObject("value", username);
        return mv;
    }

    @RequestMapping(value="/tep3")
    public ModelAndView tep3(@PathVariable String name) {
        ModelAndView mv = new ModelAndView("hello"); // 对应 demo.jsp 路径
        mv.addObject("value", name);
        return mv;
    }


    @RequestMapping("/tep4")
    public ModelAndView tep4(@RequestBody Map<String, String> u) {
        ModelAndView mv = new ModelAndView("hello"); // 对应 demo.jsp 路径
        String username = u.get("name");
        mv.addObject("value", username);
        return mv;
    }


    /**
     * 接口测试
     * @return JSON 字符串
     */
        @RequestMapping("/demos")
    @ResponseBody
    public List<Demo> allDemo() {
        return service.selectAll();
    }
    // @ResponseBody 如果返回的是对象 会自动转为json字符串，如果返回的是String 则返回该字符串
}


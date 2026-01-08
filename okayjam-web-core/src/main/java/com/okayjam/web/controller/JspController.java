package com.okayjam.web.controller;

import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * @description: ${description}
 * @author: Chen wei guang
 * @create: 2018/08/08 14:43
 **/
@RequestMapping("/api/jsp")
@Controller
public class JspController {


    /**
     * JSP 测试
     *
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
    public ModelAndView tep2(@RequestParam(value = "name") String username) {
        ModelAndView mv = new ModelAndView("hello"); // 对应 demo.jsp 路径
        mv.addObject("value", username);
        return mv;
    }

    @RequestMapping(value = "/tep3")
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



}


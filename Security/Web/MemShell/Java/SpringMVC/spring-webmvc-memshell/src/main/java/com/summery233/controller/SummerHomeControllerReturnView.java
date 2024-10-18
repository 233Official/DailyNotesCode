package com.summery233.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SummerHomeControllerReturnView {
    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("message", "Summer Home Controller");
        return "home"; // 返回视图名称 "home"
    }
}

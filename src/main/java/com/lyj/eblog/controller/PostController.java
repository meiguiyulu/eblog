package com.lyj.eblog.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author LiuYunJie
 * @since 2022-02-08
 */
@Controller
//@RequestMapping("/post")
public class PostController {

    @GetMapping("/category/{id:\\d*}")
    public String category(@PathVariable(name = "id") Long id,
                           HttpServletRequest request, HttpServletResponse response) {
        request.setAttribute("currentCategoryId", id);
        return "post/category";
    }

    @GetMapping("/post/{id:\\d*}")
    public String detail(@PathVariable(name = "id") Long id) {
        return "post/detail";
    }

}

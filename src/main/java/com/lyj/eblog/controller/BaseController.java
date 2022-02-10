package com.lyj.eblog.controller;

import com.lyj.eblog.service.IPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;

@Controller
public class BaseController {

    @Autowired
    HttpServletRequest request;

    @Autowired
    IPostService postService;

}

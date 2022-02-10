package com.lyj.eblog.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lyj.eblog.service.ICommentService;
import com.lyj.eblog.service.IPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;

import javax.servlet.http.HttpServletRequest;

@Controller
public class BaseController {

    @Autowired
    HttpServletRequest request;

    @Autowired
    IPostService postService;

    @Autowired
    ICommentService commentService;

    public Page getPage() {
        int pn = ServletRequestUtils.getIntParameter(request, "pn", 1);
        int size = ServletRequestUtils.getIntParameter(request, "size", 2);
        return new Page(pn, size);
    }

}

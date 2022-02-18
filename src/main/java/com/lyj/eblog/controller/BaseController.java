package com.lyj.eblog.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lyj.eblog.config.WsConfig;
import com.lyj.eblog.service.*;
import com.lyj.eblog.shiro.AccountProfile;
import org.apache.shiro.SecurityUtils;
import org.springframework.amqp.core.AmqpTemplate;
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

    @Autowired
    IUserService userService;

    @Autowired
    IUserMessageService userMessageService;

    @Autowired
    IUserCollectionService userCollectionService;

    @Autowired
    ICategoryService categoryService;

    @Autowired
    WsService wsService;

    @Autowired
    SearchService searchService;

    @Autowired
    AmqpTemplate amqpTemplate;

    public Page getPage() {
        int pn = ServletRequestUtils.getIntParameter(request, "pn", 1);
        int size = ServletRequestUtils.getIntParameter(request, "size", 2);
        return new Page(pn, size);
    }

    protected AccountProfile getProfile() {
        return (AccountProfile)SecurityUtils.getSubject().getPrincipal();
    }

    protected Long getProfileId() {
        return getProfile().getId();
    }

}

package com.lyj.eblog.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lyj.eblog.service.ICommentService;
import com.lyj.eblog.service.IPostService;
import com.lyj.eblog.service.IUserMessageService;
import com.lyj.eblog.service.IUserService;
import com.lyj.eblog.shiro.AccountProfile;
import org.apache.shiro.SecurityUtils;
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

/*    @Autowired
    UserMessageService messageService;

    @Autowired
    UserCollectionService collectionService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    WsService wsService;

    @Autowired
    SearchService searchService;

    @Autowired
    AmqpTemplate amqpTemplate;

    @Autowired
    ChatService chatService;*/

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

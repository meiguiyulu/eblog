package com.lyj.eblog.controller;


import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lyj.eblog.pojo.Post;
import com.lyj.eblog.pojo.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author LiuYunJie
 * @since 2022-02-08
 */
@Controller
public class UserController extends BaseController{

    /**
     * 个人中心
     */
    @GetMapping("/user/home")
    public String home() {
        /*当前登录用户*/
        User user = userService.getById(getProfileId());

        List<Post> posts = postService.list(new QueryWrapper<Post>()
                .eq("user_id", getProfileId())
                // 30天内
                //.gt("created", DateUtil.lastMonth()
                .orderByDesc("created")
        );

        request.setAttribute("user", user);
        request.setAttribute("posts", posts);
        return "/user/home";
    }

}

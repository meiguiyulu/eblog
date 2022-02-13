package com.lyj.eblog.service;

import com.lyj.eblog.common.lang.Result;
import com.lyj.eblog.pojo.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lyj.eblog.shiro.AccountProfile;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author LiuYunJie
 * @since 2022-02-08
 */
public interface IUserService extends IService<User> {

    /*注册功能*/
    Result register(User user);

    /*登录*/
    AccountProfile login(String username, String password);
}

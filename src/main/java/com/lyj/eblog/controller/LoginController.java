package com.lyj.eblog.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.google.code.kaptcha.Producer;
import com.lyj.eblog.common.lang.Result;
import com.lyj.eblog.pojo.User;
import com.lyj.eblog.service.IUserService;
import com.lyj.eblog.util.ValidationUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Controller
public class LoginController extends BaseController {

    private static final String KAPTCHA_SESSION_KEY = "KAPTCHA_SESSION_KEY";

    @Autowired
    Producer producer;

    @Autowired
    IUserService userService;

    @GetMapping("/kapthca.jpg")
    public void kaptcha(HttpServletResponse response) throws IOException {
        /*验证码*/
        String text = producer.createText();
        BufferedImage image = producer.createImage(text);

        /*将验证码信息存储到Session中*/
        request.getSession().setAttribute(KAPTCHA_SESSION_KEY, text);

        response.setHeader("Cache-Control",
                "no-store, no-cache");
        response.setContentType("image/ipeg");
        ServletOutputStream outputStream = response.getOutputStream();
        ImageIO.write(image, "jpg", outputStream);
    }

    @GetMapping("/login")
    public String login() {
        return "/auth/login";
    }

    @ResponseBody
    @PostMapping("/login")
    public Result doLogin(String email, String password) {
        if (StrUtil.isEmpty(email) || StrUtil.isBlank(password)) {
            return Result.fail("邮箱或密码不能为空");
        }

        UsernamePasswordToken token = new UsernamePasswordToken(email, SecureUtil.md5(password));
        try {
            SecurityUtils.getSubject().login(token);
        } catch (AuthenticationException e) {
            if (e instanceof UnknownAccountException) {
                return Result.fail("用户不存在");
            } else if (e instanceof LockedAccountException) {
                return Result.fail("用户被禁用");
            } else if (e instanceof IncorrectCredentialsException) {
                return Result.fail("密码错误");
            } else {
                return Result.fail("用户认证失败");
            }
        }

        return Result.success().action("/");
    }

    @GetMapping("/register")
    public String register() {
        return "/auth/reg";
    }

    @ResponseBody
    @PostMapping("/register")
    public Result doRegister(User user, String repass, String vercode) {

        /*检验用户名、密码、邮箱*/
        ValidationUtil.ValidResult validResult = ValidationUtil.validateBean(user);
        if (validResult.hasErrors()) {
            return Result.fail(validResult.getErrors());
        }

        /**
         * 这里如果把密码加密应该更好一些
         * */
        if (!user.getPassword().equals(repass)) {
            return Result.fail("两次输入密码不正确");
        }

        /*图片验证码*/
        String attribute = (String) request.getSession().getAttribute(KAPTCHA_SESSION_KEY);

        if (attribute == null || !attribute.equalsIgnoreCase(vercode)) {
            return Result.fail("验证码输入不正确");
        }

        /*注册功能*/
        Result result = userService.register(user);
        return result.action("/login");
    }

    /*注销*/
    @ResponseBody
    @RequestMapping("/user/logout")
    public String logout() {
        SecurityUtils.getSubject().logout();
        return "redirect:/";
    }

}

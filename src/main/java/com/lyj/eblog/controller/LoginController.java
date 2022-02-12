package com.lyj.eblog.controller;

import com.google.code.kaptcha.Producer;
import com.lyj.eblog.common.lang.Result;
import com.lyj.eblog.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

    @GetMapping("/register")
    public String register() {
        return "/auth/reg";
    }

    @ResponseBody
    @PostMapping("/register")
    public Result doRegister(User user, String repass, String vercode) {

        /*图片验证码*/
        String attribute = (String) request.getSession().getAttribute(KAPTCHA_SESSION_KEY);

        return Result.success().action("/login");
    }

}

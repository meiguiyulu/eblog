package com.lyj.eblog.controller;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lyj.eblog.Vo.UserMessageVo;
import com.lyj.eblog.common.lang.Result;
import com.lyj.eblog.pojo.Post;
import com.lyj.eblog.pojo.User;
import com.lyj.eblog.pojo.UserMessage;
import com.lyj.eblog.shiro.AccountProfile;
import com.lyj.eblog.util.UploadUtil;
import freemarker.template.utility.StringUtil;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    @Autowired
    UploadUtil uploadUtil;

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

    /*用户的基本设置*/
    @GetMapping("/user/set")
    public String set() {

        /*当前登录用户*/
        User user = userService.getById(getProfileId());

        request.setAttribute("user", user);

        return "/user/set";
    }

    @ResponseBody
    @PostMapping("/user/set")
    public Result doSet(User user) {

        if(StrUtil.isNotBlank(user.getAvatar())) {

            User temp = userService.getById(getProfileId());
            temp.setAvatar(user.getAvatar());
            userService.updateById(temp);

            AccountProfile profile = getProfile();
            profile.setAvatar(user.getAvatar());

            SecurityUtils.getSubject().getSession().setAttribute("profile", profile);

            return Result.success().action("/user/set#avatar");
        }

        if(StrUtil.isBlank(user.getUsername())) {
            return Result.fail("昵称不能为空");
        }
        long count = userService.count(new QueryWrapper<User>()
                .eq("username", getProfile().getUsername())
                .ne("id", getProfileId()));
        if(count > 0) {
            return Result.fail("该昵称已被占用");
        }

        User temp = userService.getById(getProfileId());
        temp.setUsername(user.getUsername());
        temp.setGender(user.getGender());
        temp.setSign(user.getSign());
        userService.updateById(temp);

        AccountProfile profile = getProfile();
        profile.setUsername(temp.getUsername());
        profile.setSign(temp.getSign());
        SecurityUtils.getSubject().getSession().setAttribute("profile", profile);

        return Result.success().action("/user/set");
    }

    /*上传头像*/
    @ResponseBody
    @RequestMapping("/user/upload")
    public Result uploadAvatar(@RequestParam(value = "file") MultipartFile file) throws IOException {
        return uploadUtil.upload(UploadUtil.type_avatar, file);
    }

    /*更新密码*/
    @ResponseBody
    @PostMapping("/user/repass")
    public Result repass(String nowpass, String pass, String repass) {
        if(!pass.equals(repass)) {
            return Result.fail("两次密码不相同");
        }

        User user = userService.getById(getProfileId());

        String nowPassMd5 = SecureUtil.md5(nowpass);
        if(!nowPassMd5.equals(user.getPassword())) {
            return Result.fail("密码不正确");
        }

        user.setPassword(SecureUtil.md5(pass));
        userService.updateById(user);

        return Result.success().action("/user/set");

    }

    /*用户中心*/
    @GetMapping("/user/index")
    public String index() {
        return "/user/index";
    }

    /*发表的文章*/
    @ResponseBody
    @GetMapping("/user/public")
    public Result userPublic() {
        Page page = postService.page(getPage(), new QueryWrapper<Post>()
                .eq("user_id", getProfileId())
                .orderByDesc("created"));
        return Result.success(page);
    }

    /*收藏的文章*/
    @ResponseBody
    @GetMapping("/user/collection")
    public Result collection() {
        IPage page = postService.page(getPage(), new QueryWrapper<Post>()
                .inSql("id", "SELECT post_id FROM m_user_collection where user_id = " + getProfileId())
        );
        return Result.success(page);
    }

    /*我的消息*/
    @GetMapping("/user/mess")
    public String message() {
        IPage<UserMessageVo> page = userMessageService.paging(getPage(), new QueryWrapper<UserMessage>()
                .eq("to_user_id", getProfileId())
                .orderByDesc("created"));
        request.setAttribute("pageData", page);
        // 把消息改成已读状态
        List<Long> ids = new ArrayList<>();
        for(UserMessageVo userMessageVo : page.getRecords()) {
            if(userMessageVo.getStatus() == 0) {
                ids.add(userMessageVo.getId());
            }
        }
        // 批量修改成已读
        userMessageService.updateToReaded(ids);

        request.setAttribute("pageData", page);
        return "/user/message";
    }

    @ResponseBody
    @PostMapping("/message/remove")
    public Result messageRemove(Long id,
                                @RequestParam(defaultValue = "false") Boolean all) {

        boolean remove = userMessageService.remove(new QueryWrapper<UserMessage>()
                .eq("to_user_id", getProfileId())
                .eq(!all, "id", id));
        return remove ? Result.success("删除成功") : Result.fail("删除失败");
    }

    @ResponseBody
    @RequestMapping("/message/nums")
    public Map msgNums(){
        long count = userMessageService.count(new QueryWrapper<UserMessage>()
                .eq("to_user_id", getProfileId())
                .eq("status", 0));
        return MapUtil.builder("status", 0).put("count", (int) count).build();

    }

}

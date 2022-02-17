package com.lyj.eblog.controller;


import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lyj.eblog.Vo.CommentVo;
import com.lyj.eblog.Vo.PostVo;
import com.lyj.eblog.common.lang.Result;
import com.lyj.eblog.pojo.Post;
import com.lyj.eblog.pojo.UserCollection;
import org.springframework.util.Assert;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author LiuYunJie
 * @since 2022-02-08
 */
@Controller
//@RequestMapping("/post")
public class PostController extends BaseController {

    @GetMapping("/category/{id:\\d*}")
    public String category(@PathVariable(name = "id") Long id) {
        int pn = ServletRequestUtils.getIntParameter(request, "pn", 1);
        request.setAttribute("currentCategoryId", id);
        request.setAttribute("pn", pn);
        return "post/category";
    }

    @GetMapping("/post/{id:\\d*}")
    public String detail(@PathVariable(name = "id") Long id) {

        /*文章*/
        PostVo postVo = postService.selectOnePost(new QueryWrapper<Post>()
                .eq("p.id", id));
        Assert.notNull(postVo, "文章已被删除");

        /**
         * 阅读量加1
         * */
        postService.putViewCount(postVo);


        /*评论
         *
         * 参数：1分页 2文章id 3用户id 4排序
         * */
        IPage<CommentVo> commentResults = commentService.paging(getPage(), postVo.getId(),
                null, "created");

        request.setAttribute("currentCategoryId", postVo.getCategoryId());
        request.setAttribute("post", postVo);
        request.setAttribute("commentResults", commentResults);
        return "post/detail";
    }

    /*判断用户有没有收藏这篇文章*/
    @ResponseBody
    @PostMapping("/collection/find")
    public Result collectionFind(Long pid) {

        long count = userCollectionService.count(new QueryWrapper<UserCollection>()
                .eq("user_id", getProfileId())
                .eq("post_id", pid));

        return Result.success(MapUtil.of("collection", count > 0));
    }

    /*收藏文章*/
    @ResponseBody
    @PostMapping("/collection/add")
    public Result collectionAdd(Long pid) {

        /*判断文章存不存在*/
        Post post = postService.getById(pid);
        Assert.isTrue(post!=null, "该帖子已被删除");

        /*判断文章有没有被收藏*/
        long count = userCollectionService.count(new QueryWrapper<UserCollection>()
                .eq("user_id", getProfileId())
                .eq("post_id", pid));
        if (count > 0) {
            return Result.fail("你已经收藏过这篇文章");
        }

        UserCollection userCollection = new UserCollection();
        userCollection.setUserId(getProfileId());
        userCollection.setPostUserId(pid);
        userCollection.setCreated(new Date());
        userCollection.setModified(new Date());
        userCollection.setPostUserId(post.getUserId());

        userCollectionService.save(userCollection);

        return Result.success();
    }

    /*取消收藏文章*/
    @ResponseBody
    @PostMapping("/collection/remove")
    public Result collectionRemove(Long pid) {
        /*判断文章存不存在*/
        Post post = postService.getById(pid);
        Assert.isTrue(post!=null, "该帖子已被删除");

        userCollectionService.remove(new QueryWrapper<UserCollection>()
                .eq("user_id", getProfileId())
                .eq("post_id", pid));
        return Result.success();
    }

}

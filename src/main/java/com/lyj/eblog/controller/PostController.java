package com.lyj.eblog.controller;


import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lyj.eblog.Vo.CommentVo;
import com.lyj.eblog.Vo.PostVo;
import com.lyj.eblog.common.lang.Result;
import com.lyj.eblog.pojo.*;
import com.lyj.eblog.util.ValidationUtil;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
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

        Post post = postService.getById(pid);

        Assert.isTrue(post != null, "改帖子已被删除");
        long count = userCollectionService.count(new QueryWrapper<UserCollection>()
                .eq("user_id", getProfileId())
                .eq("post_id", pid)
        );
        if (count > 0) {
            return Result.fail("你已经收藏");
        }

        UserCollection collection = new UserCollection();
        collection.setUserId(getProfileId());
        collection.setPostId(pid);
        collection.setCreated(new Date());
        collection.setModified(new Date());

        collection.setPostUserId(post.getUserId());

        userCollectionService.save(collection);
        return Result.success();
    }

    /*取消收藏文章*/
    @ResponseBody
    @PostMapping("/collection/remove")
    public Result collectionRemove(Long pid) {
        /*判断文章存不存在*/
        Post post = postService.getById(pid);
        Assert.isTrue(post != null, "该帖子已被删除");

        userCollectionService.remove(new QueryWrapper<UserCollection>()
                .eq("user_id", getProfileId())
                .eq("post_id", pid));
        return Result.success();
    }

    @GetMapping("/post/edit")
    public String edit() {
        String id = request.getParameter("id");
        if (StrUtil.isNotEmpty(id)) {
            Post post = postService.getById(id);
            Assert.isTrue(post != null, "该帖子已被删除");
            Assert.isTrue(post.getUserId().longValue() == getProfileId().longValue(), "没有权限操作此文章");
            request.setAttribute("post", post);
        }

        request.setAttribute("categories", categoryService.list());
        return "/post/edit";
    }

    @ResponseBody
    @PostMapping("/post/submit")
    public Result submit(Post post) {
        ValidationUtil.ValidResult validResult = ValidationUtil.validateBean(post);
        if (validResult.hasErrors()) {
            return Result.fail(validResult.getErrors());
        }

        if (post.getId() == null) {
            post.setUserId(getProfileId());

            post.setModified(new Date());
            post.setCreated(new Date());
            post.setCommentCount(0);
            post.setEditMode(null);
            post.setLevel(0);
            post.setRecommend(false);
            post.setViewCount(0);
            post.setVoteDown(0);
            post.setVoteUp(0);
            postService.save(post);
        } else {
            Post tempPost = postService.getById(post.getId());
            Assert.isTrue(tempPost.getUserId().longValue() == getProfileId().longValue(), "无权限编辑此文章！");

            tempPost.setTitle(post.getTitle());
            tempPost.setContent(post.getContent());
            tempPost.setCategoryId(post.getCategoryId());
            postService.updateById(tempPost);
        }

        return Result.success().action("/post/" + post.getId());
    }

    @ResponseBody
    /*@Transactional*/
    @PostMapping("/post/delete")
    public Result delete(Long id) {
        Post post = postService.getById(id);

        Assert.notNull(post, "该帖子已被删除");
        Assert.isTrue(post.getUserId().longValue() == getProfileId().longValue(), "无权限删除此文章！");

        postService.removeById(id);

        // 删除相关消息、收藏等
        userMessageService.removeByMap(MapUtil.of("post_id", id));
        userCollectionService.removeByMap(MapUtil.of("post_id", id));

/*        amqpTemplate.convertAndSend(RabbitConfig.es_exchage, RabbitConfig.es_bind_key,
                new PostMqIndexMessage(post.getId(), PostMqIndexMessage.REMOVE));*/

        return Result.success().action("/user/index");
    }

    /*回复评论*/
    @ResponseBody
/*    @Transactional*/
    @PostMapping("/post/reply/")
    public Result reply(Long jid, String content) {
        Assert.notNull(jid, "找不到对应的文章");
        Assert.hasLength(content, "评论内容不能为空");

        Post post = postService.getById(jid);
        Assert.isTrue(post != null, "该文章已被删除");

        Comment comment = new Comment();
        comment.setPostId(jid);
        comment.setContent(content);
        comment.setUserId(getProfileId());
        comment.setCreated(new Date());
        comment.setModified(new Date());
        comment.setLevel(0);
        comment.setVoteDown(0);
        comment.setVoteUp(0);
        commentService.save(comment);

        // 评论数量加一
        post.setCommentCount(post.getCommentCount() + 1);
        postService.updateById(post);

        // 本周热议数量加一
        postService.incrCommentCountAndUnionForWeekRank(post.getId(), true);

        // 通知作者，有人评论了你的文章
        // 作者自己评论自己文章，不需要通知
        if (comment.getUserId() != post.getUserId()) {
            UserMessage message = new UserMessage();
            message.setPostId(jid);
            message.setCommentId(comment.getId());
            message.setFromUserId(getProfileId());
            message.setToUserId(post.getUserId());
            message.setType(1);
            message.setContent(content);
            message.setCreated(new Date());
            message.setStatus(0);
            userMessageService.save(message);

            // 即时通知作者（websocket）
            wsService.sendMessCountToUser(message.getToUserId());
        }

        // 通知被@的人，有人回复了你的文章
        if (content.startsWith("@")) {
            String username = content.substring(1, content.indexOf(" "));
            System.out.println(username);

            User user = userService.getOne(new QueryWrapper<User>().eq("username", username));
            if (user != null) {
                UserMessage message = new UserMessage();
                message.setPostId(jid);
                message.setCommentId(comment.getId());
                message.setFromUserId(getProfileId());
                message.setToUserId(user.getId());
                message.setType(2);
                message.setContent(content);
                message.setCreated(new Date());
                message.setStatus(0);
                userMessageService.save(message);
                // 即时通知被@的用户
/*                wsService.sendMessCountToUser(message.getToUserId());*/
            }
        }
        return Result.success().action("/post/" + post.getId());
    }

}

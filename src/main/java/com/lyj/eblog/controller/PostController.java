package com.lyj.eblog.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lyj.eblog.Vo.CommentVo;
import com.lyj.eblog.Vo.PostVo;
import com.lyj.eblog.pojo.Post;
import org.springframework.util.Assert;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author LiuYunJie
 * @since 2022-02-08
 */
@Controller
//@RequestMapping("/post")
public class PostController extends BaseController{

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

}

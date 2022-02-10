package com.lyj.eblog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lyj.eblog.Vo.CommentVo;
import com.lyj.eblog.pojo.Comment;
import com.lyj.eblog.mapper.CommentMapper;
import com.lyj.eblog.pojo.User;
import com.lyj.eblog.service.ICommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author LiuYunJie
 * @since 2022-02-08
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements ICommentService {

    @Autowired
    CommentMapper commentMapper;

    @Override
    public IPage<CommentVo> paging(Page page, Long postId, User userId, String order) {
        return commentMapper.selectComments(page, new QueryWrapper<Comment>()
                .eq(postId !=null, "post_id", postId)
                .eq(userId != null, "user_id", userId)
                .orderByDesc(order != null, order));
    }
}

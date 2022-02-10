package com.lyj.eblog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lyj.eblog.Vo.CommentVo;
import com.lyj.eblog.pojo.Comment;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lyj.eblog.pojo.User;
import org.springframework.stereotype.Component;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author LiuYunJie
 * @since 2022-02-08
 */


public interface ICommentService extends IService<Comment> {

    IPage<CommentVo> paging(Page page, Long postVoId, User userId, String order);
}

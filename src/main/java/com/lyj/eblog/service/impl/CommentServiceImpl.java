package com.lyj.eblog.service.impl;

import com.lyj.eblog.pojo.Comment;
import com.lyj.eblog.mapper.CommentMapper;
import com.lyj.eblog.service.ICommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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

}

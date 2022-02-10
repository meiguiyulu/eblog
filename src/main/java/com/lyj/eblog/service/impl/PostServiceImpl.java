package com.lyj.eblog.service.impl;

import com.lyj.eblog.pojo.Post;
import com.lyj.eblog.mapper.PostMapper;
import com.lyj.eblog.service.IPostService;
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
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements IPostService {

}

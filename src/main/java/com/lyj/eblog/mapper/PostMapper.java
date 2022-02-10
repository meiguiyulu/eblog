package com.lyj.eblog.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lyj.eblog.Vo.PostVo;
import com.lyj.eblog.pojo.Post;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author LiuYunJie
 * @since 2022-02-08
 */

@Component
public interface PostMapper extends BaseMapper<Post> {

    IPage<PostVo> selectPosts(Page page,
                              @Param(Constants.WRAPPER) QueryWrapper<Post> wrapper);

    PostVo selectOnePost(@Param(Constants.WRAPPER)QueryWrapper<Post> wrapper);
}

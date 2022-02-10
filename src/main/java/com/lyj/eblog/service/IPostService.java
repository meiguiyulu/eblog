package com.lyj.eblog.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lyj.eblog.Vo.PostVo;
import com.lyj.eblog.pojo.Post;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author LiuYunJie
 * @since 2022-02-08
 */
public interface IPostService extends IService<Post> {


    /**
     * 自实现的分页功能
     * @param page          分页信息
     * @param categoryId    分类信息
     * @param userId        用户信息
     * @param level         置顶等级
     * @param recommend     是否推荐
     * @param order         排序方式
     * @return
     */
    IPage paging(Page page, Long categoryId, Long userId, Integer level,
                 Boolean recommend, String order);

    PostVo selectOnePost(QueryWrapper<Post> wrapper);
}

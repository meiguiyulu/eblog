package com.lyj.eblog.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.unit.DataSizeUtil;
import cn.hutool.core.io.unit.DataUnit;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lyj.eblog.Vo.PostVo;
import com.lyj.eblog.mapper.PostMapper;
import com.lyj.eblog.pojo.Post;
import com.lyj.eblog.service.IPostService;
import com.lyj.eblog.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    @Autowired
    PostMapper postMapper;

    @Autowired
    RedisUtil redisUtil;

    @Override
    public IPage paging(Page page, Long categoryId, Long userId,
                        Integer level, Boolean recommend, String order) {
        if (level == null) {
            level = -1;
        }
        QueryWrapper<Post> wrapper = new QueryWrapper<Post>()

                .eq(categoryId != null, "category_id", categoryId)
                .eq(userId != null, "user_id", userId)
                .eq(level == 0, "level", 0)
                .gt(level > 0, "level", 0)
                .orderByDesc(order != null, order);
        return postMapper.selectPosts(page, wrapper);
    }

    @Override
    public PostVo selectOnePost(QueryWrapper<Post> wrapper) {
        return postMapper.selectOnePost(wrapper);
    }


    /**
     * 本周热议
     */
    @Override
    public void initWeekRank() {

        /*获取7天内发表的文章*/
        List<Post> posts = this.list(new QueryWrapper<Post>()
                .ge("created", DateUtil.lastWeek())
                .select("id, title, user_id, comment_count, view_count, created"));

        /*初始化文章的总评论量*/
        for (Post post: posts) {
            String key = "day:rank:" + DateUtil.format(post.getCreated(),
                    DatePattern.PURE_DATE_FORMAT);
            redisUtil.zSet(key, post.getId(), post.getCommentCount());

            /*7天后自动过期*/
            long between = DateUtil.between(new Date(), post.getCreated(), DateUnit.DAY); // 天
            long expireTime = (7 - between) * 24 * 3600; // 剩余过期时间 秒

            redisUtil.expire(key, expireTime); /*设置key的存活时间*/

            /*缓存文章的一些基本信息(id、标题、评论数量、作者)*/
            this.hashCachePostInformation(post, expireTime);

        }

        /*并集*/
        this.zunionAndStoredLast7DayForWeekRank();

    }

    /**
     * 评论数增加以后自动更新本周热议功能
     * @param postId
     * @param isIncr
     */
    @Override
    public void incrCommentCountAndUnionForWeekRank(long postId, boolean isIncr) {
        String  currentKey = "day:rank:" + DateUtil.format(new Date(), DatePattern.PURE_DATE_FORMAT);
        redisUtil.zIncrementScore(currentKey, postId, isIncr? 1: -1);

        Post post = this.getById(postId);

        // 7天后自动过期(15号发表，7-（18-15）=4)
        long between = DateUtil.between(new Date(), post.getCreated(), DateUnit.DAY);
        long expireTime = (7 - between) * 24 * 60 * 60; // 有效时间

        // 缓存这篇文章的基本信息
        this.hashCachePostInformation(post, expireTime);

        // 重新做并集
        this.zunionAndStoredLast7DayForWeekRank();
    }

    /**
     * 阅读量加1
     * @param postVo
     */
    @Override
    public void putViewCount(PostVo postVo) {
        String key = "rank:post:" + postVo.getId();

        /*1 从缓存中获取viewCount(阅读量)*/
        Integer viewCount = (Integer) redisUtil.hget(key, "post:viewCount");

        /*2 如果没有 就从数据库中获取 再加1*/
        if (viewCount !=null) {
            postVo.setViewCount(viewCount + 1);
        } else {
            postVo.setViewCount(postVo.getViewCount() + 1);
        }

        /*3 同步到缓存中*/
        redisUtil.hset(key, "post:viewCount", postVo.getViewCount());
    }

    /**
     * 本周合并每日评论数量操作
     */
    private void zunionAndStoredLast7DayForWeekRank() {
        String  currentKey = "day:rank:" + DateUtil.format(new Date(), DatePattern.PURE_DATE_FORMAT);

        String destKey = "week:rank";
        List<String> otherKeys = new ArrayList<>();
        for(int i=-6; i < 0; i++) {
            String temp = "day:rank:" +
                    DateUtil.format(DateUtil.offsetDay(new Date(), i), DatePattern.PURE_DATE_FORMAT);

            otherKeys.add(temp);
        }

        redisUtil.zUnionAndStore(currentKey, otherKeys, destKey);
    }

    /**
     * 缓存文章的基本信息
     * @param post
     * @param expireTime
     */
    private void hashCachePostInformation(Post post, long expireTime) {
        String key = "rank:post:" + post.getId();
        boolean hasKey = redisUtil.hasKey(key);
        if (!hasKey) {
            redisUtil.hset(key, "post:id", post.getId(), expireTime);
            redisUtil.hset(key, "post:title", post.getTitle(), expireTime);
            redisUtil.hset(key, "post:commentCount", post.getCommentCount(), expireTime);
            redisUtil.hset(key, "post:viewCount", post.getViewCount(), expireTime);
        }
    }



}

package com.lyj.eblog.schedules;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lyj.eblog.pojo.Post;
import com.lyj.eblog.service.IPostService;
import com.lyj.eblog.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * 定时器功能：将缓存中的文章阅读量同步到数据库
 */
@Component
public class ViewCountSync {

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    IPostService postService;


    @Scheduled(cron = "0/5 * * * * *") //每分钟同步
    public void task() {

        Set<String> keys = redisTemplate.keys("rank:post:*");

        List<String> ids = new ArrayList<>();
        for (String key : keys) {
            if(redisUtil.hHasKey(key, "post:viewCount")){
                ids.add(key.substring("rank:post:".length()));
            }
        }

        if(ids.isEmpty()) return;

        // 需要更新阅读量
        List<Post> posts = postService.list(new QueryWrapper<Post>().in("id", ids));

        posts.stream().forEach((post) ->{
            Integer viewCount = (Integer) redisUtil.hget("rank:post:" + post.getId(), "post:viewCount");
            post.setViewCount(viewCount);
        });

        if(posts.isEmpty()) return;

        boolean isSuccess = postService.updateBatchById(posts);

        if(isSuccess) {
            ids.stream().forEach((id) -> {
                redisUtil.hdel("rank:post:" + id, "post:viewCount");
                System.out.println(id + "---------------------->同步成功");
            });
        }
    }
}

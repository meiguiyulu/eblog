package com.lyj.eblog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lyj.eblog.Vo.PostVo;
import com.lyj.eblog.search.mq.PostMqIndexMessage;
import org.springframework.stereotype.Component;

import java.util.List;


public interface SearchService {

    IPage search(Page page, String keyWord);

    int initEsData(List<PostVo> records);

    void removeIndex(PostMqIndexMessage message);

    void createOrUpdateIndex(PostMqIndexMessage message);
}

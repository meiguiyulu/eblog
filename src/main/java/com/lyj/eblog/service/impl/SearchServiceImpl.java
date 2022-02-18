package com.lyj.eblog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lyj.eblog.Vo.PostVo;
import com.lyj.eblog.pojo.Post;
import com.lyj.eblog.search.model.PostDocument;
import com.lyj.eblog.search.mq.PostMqIndexMessage;
import com.lyj.eblog.search.repository.PostRepository;
import com.lyj.eblog.service.IPostService;
import com.lyj.eblog.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    PostRepository postRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    IPostService postService;

    @Override
    public IPage search(Page page, String keyWord) {
        // 分页信息 mybatis plus的page 转成 jpa的page
        Long current = page.getCurrent() - 1;
        Long size = page.getSize();
        Pageable pageable = PageRequest.of(current.intValue(), size.intValue());

        // 搜索es得到pageData
//        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(keyWord,
//                "title", "authorName", "categoryName");
        String[] strings = new String[]{"title", "authorName", "categoryName"};

        org.springframework.data.domain.Page<PostDocument> documents =
//                postRepository.search(multiMatchQueryBuilder, pageable);
                postRepository.searchSimilar(new PostDocument(), strings, pageable);

        // 结果信息 jpa的pageData转成mybatis plus的pageData
        IPage pageData = new Page(page.getCurrent(), page.getSize(), documents.getTotalElements());
        pageData.setRecords(documents.getContent());
        return pageData;
    }

    @Override
    public int initEsData(List<PostVo> records) {
        if(records == null || records.isEmpty()) {
            return 0;
        }

        List<PostDocument> documents = new ArrayList<>();
        for(PostVo vo : records) {
            // 映射转换
            PostDocument postDocment = modelMapper.map(vo, PostDocument.class);
            documents.add(postDocment);
        }
        postRepository.saveAll(documents);
        return documents.size();
    }

    @Override
    public void createOrUpdateIndex(PostMqIndexMessage message) {
        Long postId = message.getPostId();
        PostVo postVo = postService.selectOnePost(new QueryWrapper<Post>().eq("p.id", postId));

        PostDocument postDocment = modelMapper.map(postVo, PostDocument.class);

        postRepository.save(postDocment);

        log.info("es 索引更新成功！ ---> {}", postDocment);
    }

    @Override
    public void removeIndex(PostMqIndexMessage message) {
        Long postId = message.getPostId();

        postRepository.deleteById(postId);
        log.info("es 索引删除成功！ ---> {}", message);
    }
}
